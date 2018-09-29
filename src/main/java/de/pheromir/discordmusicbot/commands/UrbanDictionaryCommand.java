package de.pheromir.discordmusicbot.commands;

import java.awt.Color;

import org.json.JSONArray;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;

public class UrbanDictionaryCommand extends Command {

	private static final String BASE_URL = "http://api.urbandictionary.com/v0/define?term={sw}";

	public UrbanDictionaryCommand() {
		this.name = "urbandictionary";
		this.aliases = new String[] { "ud" };
		this.botPermissions = new Permission[] { Permission.MESSAGE_WRITE };
		this.help = "Zeigt Definitionen zum gewählten Wort von UrbanDictionary an";
		this.guildOnly = false;
		this.category = new Category("Miscellaneous");
	}

	@Override
	protected void execute(CommandEvent e) {
		Unirest.get(BASE_URL).routeParam("sw", e.getArgs()).asJsonAsync(new Callback<JsonNode>() {

			@Override
			public void cancelled() {
				e.reply("Die Suche nach der Definition von `" + e.getArgs() + "` wurde abgebrochen.");
			}

			@Override
			public void completed(HttpResponse<JsonNode> arg0) {
				JSONArray ja = arg0.getBody().getObject().getJSONArray("list");
				if (ja == null || ja.length() == 0) {
					e.reply("Es wurde keine Definition für `" + e.getArgs() + "` gefunden.");
					return;
				} else {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("Definition: " + e.getArgs());
					eb.setAuthor("Urban Dictionary");
					eb.setColor(e.getChannelType() == ChannelType.TEXT ? e.getSelfMember().getColor() : Color.BLUE);
					for (int i = 0; i < (ja.length() <= 5 ? ja.length() : 5); i++) {
						String def = ja.getJSONObject(i).getString("definition");
						String ex = ja.getJSONObject(i).getString("example");
						eb.appendDescription("**[" + (i + 1) + "]**\n**Definition:** "
								+ def.substring(0, def.length()>200?197:def.length()) + (def.length()>200?"...":"") + "\n**Beispiel:** "
								+ ex.substring(0, ex.length()>200?197:ex.length()) + (ex.length()>200?"...":"") +"\n\n");
					}
					e.reply(eb.build());
					return;
				}

			}

			@Override
			public void failed(UnirestException arg0) {
				e.reply("Die Suche nach der Definition von `" + e.getArgs() + "` ist fehlgeschlagen: "
						+ arg0.getLocalizedMessage());
			}
		});
	}
}
