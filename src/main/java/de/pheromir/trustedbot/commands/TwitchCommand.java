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

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;

import net.dv8tion.jda.api.Permission;

public class TwitchCommand extends TrustedCommand {

	public TwitchCommand() {
		this.name = "twitch";
		this.help = "Enable/Disable/List Twitch notification if a streamer goes live.";
		this.arguments = "[username]";
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.guildOnly = true;
		this.category = new Category("Subscriptions");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if(Main.twitchToken.equals("none")) {
			e.reply("No active Twitch App Token present, Command disabled temporarily.");
			return false;
		}
		if (args.length == 0) {
			ArrayList<String> streams = (ArrayList<String>) GuildConfig.getTwitchList().keySet().stream().filter(k -> GuildConfig.getTwitchList().get(k).contains(e.getChannel().getIdLong())).collect(Collectors.toList());
			if (streams.isEmpty()) {
				e.reply("There are currently no twitch notifications active for this channel.");
				return false;
			} else {
				StringBuilder sb = new StringBuilder();
				for (String str : streams) {
					sb.append("`" + str + "`, ");
				}
				String msg = sb.substring(0, sb.length() - 2);
				e.reply("There are currently the following twitch notifications active: " + msg);
				return false;
			}
		}
		if (args.length != 1) {
			e.reply(usage);
			return false;
		} else {
			if (GuildConfig.getTwitchList().containsKey(e.getArgs().toLowerCase())
					&& GuildConfig.getTwitchList().get(e.getArgs().toLowerCase()).contains(e.getChannel().getIdLong())) {
				GuildConfig.removeTwitchStream(e.getArgs().toLowerCase(), e.getChannel().getIdLong());
				e.reply("Twitch notifications for " + e.getArgs().toLowerCase()
						+ " are now disabled for this channel.");
			} else {
				Unirest.get("https://api.twitch.tv/helix/users?login={user}").routeParam("user", args[0]).header("Authorization", "Bearer " + Main.twitchToken).asJsonAsync(new Callback<JsonNode>() {

							@Override
							public void completed(HttpResponse<JsonNode> response) {
								if (response.getStatus() != 200) {
									e.reply("An error occurred while checking if the specified Twitch user exists.");
									Main.LOG.error("Received HTTP Code " + response.getStatus()
											+ " while checking if Twitchuser exists");
									return;
								}
								JSONObject res = response.getBody().getObject();
								if (res != null && res.getJSONArray("data") != null
										&& res.getJSONArray("data").length() > 0) {
									// TODO: Check the actual names of the results, not only the length of the response
									GuildConfig.addTwitchStream(e.getArgs().toLowerCase(), e.getChannel().getIdLong(), e.getGuild().getIdLong());
									e.reply("Twitch notifications for " + e.getArgs().toLowerCase()
											+ " are now enabled for this channel.");
								} else {
									e.reply("There doesn't seem to be a user with this name (or an error occurred)");
									return;
								}
							}

							@Override
							public void failed(UnirestException e1) {
								e.reply("An error occurred.");
								Main.LOG.error("Error checking if user " + e.getArgs() + " exists: ", e);
							}

							@Override
							public void cancelled() {
								e.reply("An error occurred.");
							}

						});
			}
			return true;
		}
	}

}
