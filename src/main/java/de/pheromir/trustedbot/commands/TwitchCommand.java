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
import net.dv8tion.jda.core.Permission;

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
	protected void exec(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		if (args.length == 0) {
			ArrayList<String> streams = (ArrayList<String>) GuildConfig.getTwitchList().keySet().stream().filter(k -> GuildConfig.getTwitchList().get(k).contains(e.getChannel().getIdLong())).collect(Collectors.toList());
			if (streams.isEmpty()) {
				e.reply("There are currently no twitch notifications active for this channel.");
				return;
			} else {
				StringBuilder sb = new StringBuilder();
				for (String str : streams) {
					sb.append("`" + str + "`, ");
				}
				String msg = sb.substring(0, sb.length() - 2);
				e.reply("There are currently the following twitch notifications active: " + msg);
				return;
			}
		}
		if (args.length != 1) {
			e.reply("Syntaxerror. Usage: `" + Main.getGuildConfig(e.getGuild()).getPrefix() + this.name
					+ " <username>`");
			return;
		} else {
			if (GuildConfig.getTwitchList().containsKey(e.getArgs().toLowerCase())
					&& GuildConfig.getTwitchList().get(e.getArgs().toLowerCase()).contains(e.getChannel().getIdLong())) {
				GuildConfig.removeTwitchStream(e.getArgs().toLowerCase(), e.getChannel().getIdLong());
				e.reply("Twitch notifications for " + e.getArgs().toLowerCase()
						+ " are now disabled for this channel.");
			} else {
				Unirest.get("https://api.twitch.tv/helix/users?login="
						+ e.getArgs()).header("client-id", Main.twitchKey).asJsonAsync(new Callback<JsonNode>() {

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
			return;
		}
	}

}
