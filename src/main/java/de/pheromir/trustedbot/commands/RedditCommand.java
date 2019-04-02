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

public class RedditCommand extends TrustedCommand {

	public RedditCommand() {
		this.name = "reddit";
		this.help = "Enable/Disable the receiving of Reddit posts for the current channel.";
		this.arguments = "<subreddit>";
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.guildOnly = true;
		this.category = new Category("Subscriptions");
	}

	@Override
	protected boolean exec(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		if (args.length == 0) {
			ArrayList<String> subreddits = (ArrayList<String>) GuildConfig.getRedditList().keySet().stream().filter(k -> GuildConfig.getRedditList().get(k).contains(e.getChannel().getIdLong())).collect(Collectors.toList());
			if (subreddits.isEmpty()) {
				e.reply("There are currently no active Reddit subscriptions in this channel.");
				return false;
			} else {
				StringBuilder sb = new StringBuilder();
				for (String str : subreddits) {
					sb.append("`" + str + "`, ");
				}
				String msg = sb.substring(0, sb.length() - 2);
				e.reply("Currently active Reddit subscriptions in this channel: " + msg);
				return false;
			}
		}
		if (args.length != 1) {
			e.reply("Syntaxerror. Usage: `"+Main.getGuildConfig(e.getGuild()).getPrefix()+this.name+" <subreddit>`");
			return false;
		} else {
			if (GuildConfig.getRedditList().containsKey(e.getArgs().toLowerCase())
					&& GuildConfig.getRedditList().get(e.getArgs().toLowerCase()).contains(e.getChannel().getIdLong())) {
				GuildConfig.removeSubreddit(e.getArgs().toLowerCase(), e.getChannel().getIdLong());
				e.reply("Subreddit " + e.getArgs().toLowerCase() + " is now disabled for this channel.");
			} else {
				Unirest.get("https://www.reddit.com/r/" + e.getArgs() + "/hot/.json").asJsonAsync(new Callback<JsonNode>() {

					@Override
					public void completed(HttpResponse<JsonNode> response) {
						if(response.getStatus() != 200) {
							Main.LOG.error("Reddit-Existance-Checker received HTTP Code " + response.getStatus() + " for Subreddit " + e.getArgs());
							return;
						}
						JSONObject jo = response.getBody().getObject();
						if (jo.has("error") || (jo.has("data") && jo.getJSONObject("data").has("children")
								&& jo.getJSONObject("data").getJSONArray("children").length() == 0)) {
							e.reply("The specified subreddit does not exist. (Or an error occurred)");
							return;
						}
						GuildConfig.addSubreddit(e.getArgs().toLowerCase(), e.getChannel().getIdLong(), e.getGuild().getIdLong());
						e.reply("Subreddit " + e.getArgs().toLowerCase() + " is now enabled for this channel.");
					}

					@Override
					public void failed(UnirestException e1) {
						Main.LOG.error("Reddit-Existance-Checker for Subreddit " + e.getArgs() + " failed: ", e);
					}

					@Override
					public void cancelled() {
						Main.LOG.error("Reddit-Existance-Checker for Subreddit " + e.getArgs() + " cancelled.");
					}
					
				});
			}
			return true;
		}
	}

}
