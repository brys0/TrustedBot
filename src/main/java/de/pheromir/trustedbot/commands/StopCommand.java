package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import net.dv8tion.jda.core.Permission;

public class StopCommand extends Command {

	public StopCommand() {
		this.name = "stop";
		this.help = "Stop the playback.";
		this.guildOnly = true;
		this.category = new Category("Music");
	}

	@Override
	protected void execute(CommandEvent e) {
		if (!Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getAuthor().getIdLong())
				&& !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
			e.reply("You need DJ privileges to stop the playback.");
			return;
		}
		e.getGuild().getAudioManager().closeAudioConnection();
		Main.getGuildConfig(e.getGuild()).player.setPaused(false);
		Main.getGuildConfig(e.getGuild()).player.stopTrack();
		e.reactSuccess();
	}

}
