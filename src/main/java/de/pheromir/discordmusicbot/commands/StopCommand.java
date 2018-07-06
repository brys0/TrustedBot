package de.pheromir.discordmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;
import net.dv8tion.jda.core.Permission;

public class StopCommand extends Command {

	public StopCommand() {
		this.name = "stop";
		this.help = "Musikwiedergabe stoppen";
		this.guildOnly = true;
		this.category = new Category("Music");
	}
	
	@Override
	protected void execute(CommandEvent e) {
		if (e.getAuthor().isBot())
			return;
		if(!Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getAuthor().getIdLong()) && !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
			e.reply("Du musst mind. DJ sein um den Bot stoppen zu können.");
			return;
		}
		
		Main.getGuildAudioPlayer(e.getGuild()).player.setPaused(false);
		Main.getGuildAudioPlayer(e.getGuild()).player.stopTrack();
		e.reactSuccess();
		e.getGuild().getAudioManager().closeAudioConnection();
		
	}
	
	

}
