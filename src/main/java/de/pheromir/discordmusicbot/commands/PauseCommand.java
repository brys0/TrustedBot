package de.pheromir.discordmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;
import net.dv8tion.jda.core.Permission;

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
		if (!Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getAuthor().getIdLong()) && !e.isOwner() && !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
			e.reply("Du musst mind. DJ sein um den Bot pausieren zu können.");
			return;
		}
		
		Main.getGuildAudioPlayer(e.getGuild()).player.setPaused(true);
		e.reactSuccess();
	}
}
