package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;

public class SkipCommand extends TrustedCommand {

	public SkipCommand() {
		this.name = "skip";
		this.help = "Skip the current track.";
		this.guildOnly = true;
		this.category = new Category("Music");
	}

	@Override
	protected boolean exec(CommandEvent e) {
		GuildConfig m = Main.getGuildConfig(e.getGuild());

		if (e.getArgs().isEmpty()) {
			if (!Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getAuthor().getIdLong())
					&& m.scheduler.getCurrentRequester() != e.getAuthor()
					&& !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
				e.reply("You can only skip tracks that you requested.");
				return false;
			}
			m.scheduler.nextTrack();
			e.reactSuccess();
			return true;
		} else {
			int index = Integer.MAX_VALUE;
			try {
				index = Integer.parseInt(e.getArgs()) - 1;
			} catch (NumberFormatException ex) {
				e.reply("Please enter a number between 1 - "+(m.scheduler.getRequestedTitles().size()+1)+".");
				return false;
			}
			if (index >= m.scheduler.getRequestedTitles().size() || index < 0) {
				e.reply("Please enter a number between 1 - "+(m.scheduler.getRequestedTitles().size()+1)+".");
				return false;
			}
			if (!Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getAuthor().getIdLong()) && !e.isOwner()
					&& m.scheduler.getRequestedTitles().get(index).getRequestor() != e.getAuthor()
					&& !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
				e.reply("You can only skip tracks that you requested.");
				return false;
			}
			String title = m.scheduler.getRequestedTitles().get(index).getTrack().getInfo().title;
			if (m.scheduler.skipTrackNr(index)) {
				e.reply("`" + title + "` skipped.");
				return true;
			} else {
				e.reply("Oops, looks like something went wrong.");
				return false;
			}
		}
	}

}
