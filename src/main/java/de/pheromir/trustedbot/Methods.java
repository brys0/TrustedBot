package de.pheromir.trustedbot;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.json.JSONException;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Methods {

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
		if (days > 0)
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
		if (days > 0)
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

	static final Pattern TIMESTAMP_PATTERN = Pattern.compile("^(\\d?\\d)(?::([0-5]?\\d))?(?::([0-5]?\\d))?$");

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

	@Nullable
	public static String getRandomAvatarURL() {
		try {
			return Unirest.get("https://nekos.life/api/v2/img/avatar").asJson().getBody().getObject().getString("url");
		} catch (JSONException | UnirestException e) {
			Main.LOG.error("Error getting random Avatar-Image");
			return null;
		}
	}

	public static void mySQLQuery(String query) {
		MySQL sql = Main.getMySQL();
		sql.openConnection();
		sql.queryUpdate(query);
		sql.closeConnection();
	}

}
