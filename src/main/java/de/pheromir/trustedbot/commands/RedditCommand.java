package de.pheromir.trustedbot.commands;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.Methods;
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
	protected void exec(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		if (args.length == 0) {
			ArrayList<String> subreddits = (ArrayList<String>) GuildConfig.getRedditList().keySet().stream().filter(k -> GuildConfig.getRedditList().get(k).contains(e.getChannel().getIdLong())).collect(Collectors.toList());
			if (subreddits.isEmpty()) {
				e.reply("There are currently no active Reddit subscriptions in this channel.");
				return;
			} else {
				StringBuilder sb = new StringBuilder();
				for (String str : subreddits) {
					sb.append("`" + str + "`, ");
				}
				String msg = sb.substring(0, sb.length() - 2);
				e.reply("Currently active Reddit subscriptions in this channel: " + msg);
				return;
			}
		}
		if (args.length != 1) {
			e.reply("Syntaxerror. Usage: `"+Main.getGuildConfig(e.getGuild()).getPrefix()+this.name+" <subreddit>`");
			return;
		} else {
			if (GuildConfig.getRedditList().containsKey(e.getArgs().toLowerCase())
					&& GuildConfig.getRedditList().get(e.getArgs().toLowerCase()).contains(e.getChannel().getIdLong())) {
				GuildConfig.removeSubreddit(e.getArgs().toLowerCase(), e.getChannel().getIdLong());
				e.reply("Subreddit " + e.getArgs().toLowerCase() + " is now disabled for this channel.");
			} else {
				if (!Methods.doesSubredditExist(e.getArgs())) {
					e.reply("The specified subreddit does not exist. (Or an error occurred)");
					return;
				}
				GuildConfig.addSubreddit(e.getArgs().toLowerCase(), e.getChannel().getIdLong(), e.getGuild().getIdLong());
				e.reply("Subreddit " + e.getArgs().toLowerCase() + " is now enabled for this channel.");
			}
			return;
		}
	}

}
