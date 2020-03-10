package de.pheromir.trustedbot.commands;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Pair;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class ChangelogCommand extends TrustedCommand {

	static ArrayList<Pair<String, LocalDateTime>> changelogs = new ArrayList<Pair<String, LocalDateTime>>();

	public ChangelogCommand() {
		this.name = "changelog";
		this.aliases = new String[] { "cl" };
		this.arguments = "[nr]";
		this.help = "Show changelog(s)";
		this.category = new Category("Miscellaneous");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		int changelogsize = changelogs.size();
		if (changelogsize != 0) {
			int toShow = changelogsize - 1;
			if (args.length > 0) {
				try {
					toShow = Integer.parseInt(args[0]) - 1;
				} catch (NumberFormatException ex) {
					toShow = changelogsize - 1;
				}
			}
			if (toShow >= changelogsize) {
				toShow = changelogsize - 1;
			}
			e.reply(getChangelogEmbed(toShow, gc==null?e.getSelfMember().getAsMention():gc.getPrefix()));
		} else {
			e.reply("There are currently no changelogs to display.");
		}
		return false;
	}

	private MessageEmbed getChangelogEmbed(int index, String prefix) {
		if (changelogs.size() > index) {
			EmbedBuilder eb = new EmbedBuilder();
			Pair<String, LocalDateTime> p = changelogs.get(index);
			eb.setTitle("Changelog #" + (index + 1) + "/" + changelogs.size());
			eb.setDescription(p.getKey().replace("%p%", prefix));
			eb.setTimestamp(p.getValue().atZone(ZoneId.of("Europe/Berlin")));
			return eb.build();
		} else
			return null;
	}
	
	/* I'm fucking lazy, so I'm hardcoding the changelog here */
	static {
		changelogs.add(new Pair<String, LocalDateTime>("- Added %p%changelog command",
				LocalDateTime.of(2019, 12, 14, 20, 46)));
	}

}
