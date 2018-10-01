package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import net.dv8tion.jda.core.Permission;

public class PauseCommand extends Command {

	public PauseCommand() {
		this.name = "pause";
		this.help = "Pause the current track.";
		this.guildOnly = true;
		this.category = new Category("Music");
	}

	@Override
	protected void execute(CommandEvent e) {
		if (!Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getAuthor().getIdLong()) && !e.isOwner()
				&& !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
			e.reply("You need DJ privileges to pause the playback.");
			return;
		}

		Main.getGuildConfig(e.getGuild()).player.setPaused(true);
		e.reactSuccess();
	}
}
