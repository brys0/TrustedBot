package de.pheromir.discordmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;
import net.dv8tion.jda.core.Permission;

public class VolumeCommand extends Command {

	public VolumeCommand() {
		this.name = "volume";
		this.aliases = new String[] { "vol" };
		this.help = "Musiklautstärke anpassen";
		this.guildOnly = true;
	}

	@Override
	protected void execute(CommandEvent e) {
		if (e.getAuthor().isBot())
			return;
		if(e.getArgs().isEmpty()) {
			e.reply("Derzeitige Lautstärke: "+Main.getGuildAudioPlayer(e.getGuild()).player.getVolume());
			return;
		}
		if (!Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getAuthor().getIdLong()) && !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
			e.reply("Du musst mind. DJ sein um die Lautstärke anpassen zu können");
			return;
		}

		int vol = 0;
		try {
			vol = Integer.parseInt(e.getArgs());
		} catch (NumberFormatException ex) {
			e.reply("Ungültiger Wert.");
			return;
		}
		Main.getGuildAudioPlayer(e.getGuild()).setVolume(vol);
		e.reactSuccess();

	}

}
