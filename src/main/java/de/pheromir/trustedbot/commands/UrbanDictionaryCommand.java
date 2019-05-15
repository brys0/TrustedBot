/*******************************************************************************
 * Copyright (C) 2019 Pheromir
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package de.pheromir.trustedbot.commands;

import java.awt.Color;

import org.json.JSONArray;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;

public class UrbanDictionaryCommand extends TrustedCommand {

	private static final String BASE_URL = "http://api.urbandictionary.com/v0/define?term={sw}";

	public UrbanDictionaryCommand() {
		this.name = "urbandictionary";
		this.aliases = new String[] { "ud" };
		this.botPermissions = new Permission[] { Permission.MESSAGE_WRITE };
		this.help = "Show the definitions of the specified word from the UrbanDictionary.";
		this.guildOnly = false;
		this.category = new Category("Miscellaneous");
		this.cooldown = 30;
		this.cooldownScope = CooldownScope.USER_GUILD;
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (args.length == 0) {
			e.reply(usage);
			return false;
		}
		Unirest.get(BASE_URL).routeParam("sw", e.getArgs()).asJsonAsync(new Callback<JsonNode>() {

			@Override
			public void cancelled() {
				e.reply("The search for a definition of `" + e.getArgs() + "` has been cancelled.");
			}

			@Override
			public void completed(HttpResponse<JsonNode> arg0) {
				JSONArray ja = arg0.getBody().getObject().getJSONArray("list");
				if (ja == null || ja.length() == 0) {
					e.reply("Could not find a definition for `" + e.getArgs() + "`.");
					return;
				} else {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("Definition of `" + e.getArgs() + "`");
					eb.setAuthor("Urban Dictionary");
					eb.setColor(e.getChannelType() == ChannelType.TEXT ? e.getSelfMember().getColor() : Color.BLUE);
					for (int i = 0; i < (ja.length() <= 5 ? ja.length() : 5); i++) {
						String def = ja.getJSONObject(i).getString("definition");
						String ex = ja.getJSONObject(i).getString("example");
						eb.appendDescription("**[" + (i + 1) + "]**\n**Definition:** "
								+ def.substring(0, def.length() > 200 ? 197 : def.length())
								+ (def.length() > 200 ? "..." : "") + "\n**Example:** "
								+ ex.substring(0, ex.length() > 200 ? 197 : ex.length())
								+ (ex.length() > 200 ? "..." : "") + "\n\n");
					}
					e.reply(eb.build());
					return;
				}

			}

			@Override
			public void failed(UnirestException arg0) {
				e.reply("An error occurred while getting the definition of `" + e.getArgs() + "`: "
						+ arg0.getLocalizedMessage());
			}
		});
		return true;
	}
}
