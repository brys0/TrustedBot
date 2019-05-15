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
import de.pheromir.trustedbot.misc.RedditSubscription;
import de.pheromir.trustedbot.misc.RedditSubscription.SortType;
import net.dv8tion.jda.core.Permission;

public class RedditCommand extends TrustedCommand {

	public RedditCommand() {
		this.name = "reddit";
		this.help = "Enable/Disable the receiving of Reddit posts for the current channel.";
		this.arguments = "<subreddit> [hot/new]";
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.guildOnly = true;
		this.category = new Category("Subscriptions");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (args.length == 0) {
			ArrayList<String> subreddits = (ArrayList<String>) GuildConfig.getRedditList().keySet().stream().filter(k -> GuildConfig.getRedditList().get(k).containsChannel(e.getChannel().getIdLong())).collect(Collectors.toList());
			if (subreddits.isEmpty()) {
				e.reply("There are currently no active Reddit subscriptions in this channel.");
				return false;
			} else {
				StringBuilder sb = new StringBuilder();
				for (String str : subreddits) {
					sb.append("`" + str + " ("
							+ GuildConfig.getRedditList().get(str).getSortType(e.getChannel().getIdLong()).name().toLowerCase()
							+ ")`, ");
				}
				String msg = sb.substring(0, sb.length() - 2);
				e.reply("Currently active Reddit subscriptions in this channel: " + msg);
				return false;
			}
		}
		if (args.length != 1 && args.length != 2) {
			e.reply("Syntax error. Usage: `" + gc.getPrefix() + this.name + " " + this.arguments + "`");
			return false;
		} else {
			if (args.length == 2 && !args[1].equalsIgnoreCase("hot") && !args[1].equalsIgnoreCase("new")
					&& !args[1].equalsIgnoreCase("best")) {
				e.reply("Syntax error. Usage: `" + gc.getPrefix() + this.name + " " + this.arguments + "`");
				return false;
			}
			if (GuildConfig.getRedditList().containsKey(args[0].toLowerCase())
					&& GuildConfig.getRedditList().get(args[0].toLowerCase()).containsChannel(e.getChannel().getIdLong())) {
				GuildConfig.removeSubreddit(args[0].toLowerCase(), e.getChannel().getIdLong());
				e.reply("Subreddit " + args[0].toLowerCase() + " is now disabled for this channel.");
			} else {
				Unirest.get("https://www.reddit.com/r/" + args[0] + "/hot/.json").asJsonAsync(new Callback<JsonNode>() {

					@Override
					public void completed(HttpResponse<JsonNode> response) {
						if (response.getStatus() != 200) {
							Main.LOG.error("Reddit-Existance-Checker received HTTP Code " + response.getStatus()
									+ " for Subreddit " + args[0]);
							return;
						}
						JSONObject jo = response.getBody().getObject();
						if (jo.has("error") || (jo.has("data") && jo.getJSONObject("data").has("children")
								&& jo.getJSONObject("data").getJSONArray("children").length() == 0)) {
							e.reply("The specified subreddit does not exist. (Or an error occurred)");
							return;
						}
						GuildConfig.addSubreddit(args[0].toLowerCase(), e.getChannel().getIdLong(), e.getGuild().getIdLong(), args.length == 2
								? RedditSubscription.SortType.valueOf(args[1].toUpperCase())
								: SortType.HOT);
						e.reply("Subreddit " + args[0].toLowerCase() + " is now enabled for this channel.");
					}

					@Override
					public void failed(UnirestException e1) {
						Main.LOG.error("Reddit-Existance-Checker for Subreddit " + args[0] + " failed: ", e);
					}

					@Override
					public void cancelled() {
						Main.LOG.error("Reddit-Existance-Checker for Subreddit " + args[0] + " cancelled.");
					}

				});
			}
			return true;
		}
	}

}
