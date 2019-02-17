package de.pheromir.trustedbot;

import java.io.IOException;
import java.net.ConnectException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import de.pheromir.trustedbot.exceptions.HttpErrorException;

public class Methods {

	static final Pattern TIMESTAMP_PATTERN = Pattern.compile("^(\\d?\\d)(?::([0-5]?\\d))?(?::([0-5]?\\d))?$");

	/*
	 * GENERAL HTTP CONNECTIONS
	 */

	public static JSONObject httpRequestJSON(String url) throws HttpErrorException, InterruptedException, ExecutionException, TimeoutException {
		String resp = httpRequest(url);
		if (resp.isEmpty()) {
			throw new HttpErrorException("An error occurred during the request: empty response");
		} else {
			JSONObject myResponse = new JSONObject(resp);
			return myResponse;
		}

	}

	public static String httpRequest(String url) throws InterruptedException, ExecutionException, TimeoutException, HttpErrorException {
		Future<HttpResponse<String>> future = Unirest.get(url).asStringAsync();

			HttpResponse<String> r = future.get(1, TimeUnit.MINUTES);
			if (r.getStatus() == 404) {
				throw new HttpErrorException();
			}
			return r.getBody();

	}

	/*
	 * CONVERT TIMEMILLIS INTO A HH:mm:ss STRING
	 */

	public static String getTimeString(long millis) {
		if (millis == Long.MAX_VALUE || millis == 0L)
			return "Stream";
		long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        if(days > 0)
        	return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
		if (hours > 0)
			return String.format("%02d:%02d:%02d", hours, minutes, seconds);
		else
			return String.format("%02d:%02d", minutes, seconds);
	}
	
	public static String getTimeString2(long millis) {
		if (millis == Long.MAX_VALUE || millis == 0L)
			return "Stream";
		long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        minutes++;
        if(days > 0)
        	return String.format("%02d:%02d:%02d", days, hours, minutes);
		if (hours > 0)
			return String.format("%02d:%02d", hours, minutes);
		else
			return String.format("%02d", minutes);
	}

	/*
	 * CONVERT TIMESTRING INTO TIMEMILLIS Method Copyright (c) 2017 Frederik Ar.
	 * Mikkelsen
	 * https://github.com/Frederikam/FredBoat/blob/dev/FredBoat/src/main/java/
	 * fredboat/util/TextUtils.java
	 */

	public static long parseTimeString(String str) throws NumberFormatException {
		long millis = 0;
		long seconds = 0;
		long minutes = 0;
		long hours = 0;

		Matcher m = TIMESTAMP_PATTERN.matcher(str);

		m.find();

		int capturedGroups = 0;
		if (m.group(1) != null)
			capturedGroups++;
		if (m.group(2) != null)
			capturedGroups++;
		if (m.group(3) != null)
			capturedGroups++;

		switch (capturedGroups) {
			case 0:
				throw new IllegalStateException("Unable to match " + str);
			case 1:
				seconds = Integer.parseInt(m.group(1));
				break;
			case 2:
				minutes = Integer.parseInt(m.group(1));
				seconds = Integer.parseInt(m.group(2));
				break;
			case 3:
				hours = Integer.parseInt(m.group(1));
				minutes = Integer.parseInt(m.group(2));
				seconds = Integer.parseInt(m.group(3));
				break;
		}

		minutes = minutes + hours * 60;
		seconds = seconds + minutes * 60;
		millis = seconds * 1000;

		return millis;
	}

	/*
	 * CONVERT YOUTUBE DURATION TO TIMEMILLIS
	 */

	public static long getYoutubeDuration(String videoId) {
		YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
				new HttpRequestInitializer() {

					public void initialize(HttpRequest request) throws IOException {
					}
				}).setApplicationName("DiscordBot").build();

		YouTube.Videos.List videoRequest;
		try {
			videoRequest = youtube.videos().list("contentDetails");
			videoRequest.setId(videoId);
			videoRequest.setKey(Main.youtubeKey);
			VideoListResponse listResponse = videoRequest.execute();
			List<Video> videoList = listResponse.getItems();

			Video targetVideo = videoList.iterator().next();
			return Duration.parse(targetVideo.getContentDetails().getDuration()).toMillis();

		} catch (IOException e) {
			Main.LOG.error("", e);
		}
		return 0;
	}

	public static boolean doesSubredditExist(String subreddit) {
		Callable<Boolean> task = () -> {
			JSONObject jo = httpRequestJSON("https://www.reddit.com/r/" + subreddit + "/hot/.json");
			if (jo.has("error") || (jo.has("data") && jo.getJSONObject("data").has("children")
					&& jo.getJSONObject("data").getJSONArray("children").length() == 0)) {
				return false;
			}
			return true;
		};
		Future<Boolean> future = Executors.newCachedThreadPool().submit(task);
		try {
			return future.get(5, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			return false;
		}
	}

	/*
	 * CHECK IF A TWITCH USER EXISTS
	 */

	public static boolean doesTwitchUserExist(String twitchname) {
		Future<HttpResponse<String>> future = Unirest.get("https://api.twitch.tv/helix/users?login="
				+ twitchname).header("client-id", Main.twitchKey).asStringAsync();
		try {
			HttpResponse<String> r = future.get(1, TimeUnit.MINUTES);
			if (r.getStatus() == 404) {
				return false;
			}
			JSONObject res = new JSONObject(r.getBody().toString());
			if (res.getJSONArray("data").length() == 0) {
				return false;
			}
			return true;
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			Main.LOG.error("", e);
			return false;
		}
	}

	/*
	 * GET THE STREAM INFO OF A TWITCH USER, IF STREAMING
	 */

	public static JSONObject getStreamInfo(String twitchname) throws ConnectException {
		Future<HttpResponse<String>> future = Unirest.get("https://api.twitch.tv/kraken/streams/" + twitchname
				+ "?stream_type=live").header("client-id", Main.twitchKey).asStringAsync();
		try {
			HttpResponse<String> r = future.get(30, TimeUnit.SECONDS);
			if (r.getStatus() == 404)
				return null;
			JSONObject res = new JSONObject(r.getBody().toString());
			return res;
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			Main.LOG.error("", e);
			return null;
		}
	}
	
	
	
	public static String getRandomAvatarURL() throws JSONException, HttpErrorException, InterruptedException, ExecutionException, TimeoutException {
		return Methods.httpRequestJSON("https://nekos.life/api/v2/img/avatar").getString("url");
	}
	
	public static String getRandomLizardURL() throws JSONException, HttpErrorException, InterruptedException, ExecutionException, TimeoutException {
		return Methods.httpRequestJSON("https://nekos.life/api/v2/img/lizard").getString("url");
	}
	

	public static void mySQLQuery(String query) {
		MySQL sql = Main.getMySQL();
		sql.openConnection();
		sql.queryUpdate(query);
		sql.closeConnection();
	}

}
