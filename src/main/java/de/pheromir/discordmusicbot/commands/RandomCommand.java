package de.pheromir.discordmusicbot.commands;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Methods;

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
		JSONArray joa = Methods.GiphySearchRequest(BASE_URL, e.getArgs()).getJSONArray("data");
		if(joa.length() < 1) {
			e.reply("Es wurden keine Ergebnisse zu dem Suchquery gefunden.");
			return;
		}
		int i = new Random().nextInt(joa.length());
		JSONObject jo = joa.getJSONObject(i);
		e.reply(jo.getString("title")+" ["+(i+1)+"/"+joa.length()+"]\n"+jo.getString("embed_url"));
	}

}
