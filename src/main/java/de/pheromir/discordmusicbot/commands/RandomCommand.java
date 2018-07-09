package de.pheromir.discordmusicbot.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;

public class RandomCommand extends Command {
	private String BASE_URL = "http://api.giphy.com/v1/gifs/search";

	public RandomCommand() {
		this.name = "random";
		this.help = "Zeigt ein zufälliges Gif";
		this.guildOnly = false;
		this.category = new Category("RandomImage");
	}

	@Override
	protected void execute(CommandEvent e) {
		JSONArray joa = giphySearchRequest(BASE_URL, e.getArgs()).getJSONArray("data");
		if(joa.length() < 1) {
			e.reply("Es wurden keine Ergebnisse zu dem Suchquery gefunden.");
			return;
		}
		int i = new Random().nextInt(joa.length());
		JSONObject jo = joa.getJSONObject(i);
		e.reply(jo.getString("title")+" ["+(i+1)+"/"+joa.length()+"]\n"+jo.getString("embed_url"));
	}
	
	public JSONObject giphySearchRequest(String url, String tag) {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
