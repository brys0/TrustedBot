package de.pheromir.discordmusicbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

public class Methods {

	/*
	 * GENERAL JSON HTTP CONNECTIONS
	 */

	public static JSONObject httpRequest(String url) throws IOException {
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setReadTimeout(60000);
			con.setConnectTimeout(60000);
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			JSONObject myResponse = new JSONObject(response.toString());
			return myResponse;
		} catch (IOException e) {
			throw e;
		}
	}

	/*
	 * CONVERT TIMEMILLIS INTO A HH:mm:ss STRING
	 */

	public static String getTimeString(long millis) {
		if (millis == Long.MAX_VALUE || millis == 0L)
			return "Stream";
		long second = (millis / 1000) % 60;
		long minute = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60)) % 24;
		if (hour > 0)
			return String.format("%02d:%02d:%02d", hour, minute, second);
		else
			return String.format("%02d:%02d", minute, second);
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
			e.printStackTrace();
		}
		return 0;
	}
	
	public static boolean doesSubredditExist(String subreddit) {
		Callable<Boolean> task = () -> {
			try {
				JSONObject jo = httpRequest("https://www.reddit.com/r/"+subreddit+"/hot/.json");
				if(jo.has("error") || (jo.has("data") && jo.getJSONObject("data").has("children") && jo.getJSONObject("data").getJSONArray("children").length() == 0)) {
					return false;
				}
				return true;
			} catch (IOException e) {
				return false;
			}
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
		try {
			URL obj = new URL("https://api.twitch.tv/helix/users?login=" + twitchname);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("client-id", Main.twitchKey);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			JSONObject res = new JSONObject(response.toString());
			if (res.getJSONArray("data").length() == 0) {
				return false;
			}
			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * GET THE STREAM INFO OF A TWITCH USER, IF STREAMING
	 */

	public static JSONObject getStreamInfo(String twitchname) {
		try {
			URL obj = new URL("https://api.twitch.tv/kraken/streams/" + twitchname+"?stream_type=live");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("client-id", Main.twitchKey);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			JSONObject res = new JSONObject(response.toString());
			return res;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
