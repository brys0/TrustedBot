package de.pheromir.discordmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;

public class StopCommand extends Command {

	public StopCommand() {
		this.name = "stop";
		this.help = "Musikwiedergabe stoppen";
		this.guildOnly = true;
	}
	
	@Override
	protected void execute(CommandEvent e) {
		if (e.getAuthor().isBot())
			return;
		if(!Main.djs.contains(e.getAuthor().getId()) && !e.isOwner()) {
			e.reply("Du musst ein DJ sein um den Bot stoppen zu können.");
			return;
		}
		
		Main.getGuildAudioPlayer(e.getGuild()).player.setPaused(false);
		Main.getGuildAudioPlayer(e.getGuild()).player.stopTrack();
		e.reactSuccess();
		e.getGuild().getAudioManager().closeAudioConnection();
		
	}
	
	

}
