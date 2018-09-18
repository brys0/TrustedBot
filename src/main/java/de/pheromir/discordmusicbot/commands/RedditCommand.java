package de.pheromir.discordmusicbot.commands;

import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Methods;
import de.pheromir.discordmusicbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;

public class RedditCommand extends Command {

	public RedditCommand() {
		this.name = "reddit";
		this.help = "Subreddit-Posts im Channel de-/aktivieren.";
		this.arguments = "<Subreddit>";
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.guildOnly = true;
	}

	@Override
	protected void execute(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		if (args.length == 0) {
			ArrayList<String> subreddits = new ArrayList<>();
			for (String str : GuildConfig.getRedditList().keySet()) {
				if (GuildConfig.getRedditList().get(str).contains(e.getChannel().getIdLong())) {
					subreddits.add(str);
				}
			}
			if (subreddits.isEmpty()) {
				e.reply("In diesem Channel sind momenten keine Subreddits aktiv.");
				return;
			} else {
				StringBuilder sb = new StringBuilder();
				for (String str : subreddits) {
					sb.append("`" + str + "`, ");
				}
				String msg = sb.substring(0, sb.length() - 2);
				e.reply("In diesem Channel sind momentan folgende Subreddits aktiv: " + msg);
				return;
			}
		}
		if (args.length != 1) {
			e.reply("Syntaxfehler. Verwendung: `!reddit <Subreddit>`");
			return;
		} else {
			if (GuildConfig.getRedditList().containsKey(e.getArgs().toLowerCase())
					&& GuildConfig.getRedditList().get(e.getArgs().toLowerCase()).contains(e.getChannel().getIdLong())) {
				GuildConfig.removeSubreddit(e.getArgs().toLowerCase(), e.getChannel().getIdLong());
				e.reply("Subreddit " + e.getArgs().toLowerCase() + " ist in diesem Channel nun deaktiviert.");
			} else {
				if (!Methods.doesSubredditExist(e.getArgs())) {
					e.reply("Es scheint keinen Subreddit mit diesem Namen zu geben (oder es ist ein Fehler aufgetreten).");
					return;
				}
				GuildConfig.addSubreddit(e.getArgs().toLowerCase(), e.getChannel().getIdLong(), e.getGuild().getIdLong());
				e.reply("Subreddit " + e.getArgs().toLowerCase() + " ist in diesem Channel nun aktiviert.");
			}
			return;
		}
	}

}
