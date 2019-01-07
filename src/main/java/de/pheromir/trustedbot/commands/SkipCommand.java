package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;

public class SkipCommand extends Command {

	public SkipCommand() {
		this.name = "skip";
		this.help = "Skip the current track.";
		this.guildOnly = true;
		this.category = new Category("Music");
	}

	@Override
	protected void execute(CommandEvent e) {
		if(e.getChannelType() == ChannelType.TEXT && Main.getGuildConfig(e.getGuild()).isCommandDisabled(this.name)) {
			e.reply(Main.COMMAND_DISABLED);
			return;
		}
		GuildConfig m = Main.getGuildConfig(e.getGuild());

		if (e.getArgs().isEmpty()) {
			if (!Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getAuthor().getIdLong())
					&& m.scheduler.getCurrentRequester() != e.getAuthor()
					&& !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
				e.reply("You can only skip tracks that you requested.");
				return;
			}
			m.scheduler.nextTrack();
			e.reactSuccess();
		} else {
			int index = Integer.MAX_VALUE;
			try {
				index = Integer.parseInt(e.getArgs()) - 1;
			} catch (NumberFormatException ex) {
				e.reply("Please enter a number between 1 - "+(m.scheduler.getRequestedTitles().size()+1)+".");
				return;
			}
			if (index >= m.scheduler.getRequestedTitles().size() || index < 0) {
				e.reply("Please enter a number between 1 - "+(m.scheduler.getRequestedTitles().size()+1)+".");
				return;
			}
			if (!Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getAuthor().getIdLong()) && !e.isOwner()
					&& m.scheduler.getRequestedTitles().get(index).getRequestor() != e.getAuthor()
					&& !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
				e.reply("You can only skip tracks that you requested.");
				return;
			}
			String title = m.scheduler.getRequestedTitles().get(index).getTrack().getInfo().title;
			if (m.scheduler.skipTrackNr(index)) {
				e.reply("`" + title + "` skipped.");
			} else {
				e.reply("Oops, looks like something went wrong.");
			}
		}
	}

}
