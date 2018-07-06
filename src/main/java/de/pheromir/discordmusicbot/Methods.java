package de.pheromir.discordmusicbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.List;

import org.json.JSONObject;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

public class Methods {

	public static JSONObject HttpRequest(String url) {
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
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
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static JSONObject GiphySearchRequest(String url, String tag) {
		try {
			URL obj = new URL(url + "?q=" + URLEncoder.encode(tag, "UTF-8") + "&rating=nsfw&limit=50");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("api_key", Main.giphyKey);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			JSONObject myResponse = new JSONObject(response.toString());
			return myResponse;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

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

	public static long getYoutubeDuration(String videoId) {
		YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {

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

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return 0;
	}
	
	public static boolean doesTwitchUserExist(String twitchname) {
		try {
			URL obj = new URL("https://api.twitch.tv/helix/users?login="+twitchname);
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
			if(res.getJSONArray("data").length() == 0) {
				return false;
			} return true;
			
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
	}
	
	public static JSONObject getStreamInfo(String twitchname) {
		try {
			URL obj = new URL("https://api.twitch.tv/kraken/streams/"+twitchname);
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
			
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}

}
