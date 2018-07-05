package de.pheromir.discordmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;

public class PauseCommand extends Command {

	public PauseCommand() {
		this.name = "pause";
		this.help = "Musikwiedergabe pausieren";
		this.guildOnly = true;
	}

	@Override
	protected void execute(CommandEvent e) {
		if (e.getAuthor().isBot())
			return;
		if (!Main.djs.contains(e.getAuthor().getId()) && !e.isOwner()) {
			e.reply("Du musst ein DJ sein um den Bot pausieren zu können.");
			return;
		}
		
		Main.getGuildAudioPlayer(e.getGuild()).player.setPaused(true);
		e.reactSuccess();
	}
}
