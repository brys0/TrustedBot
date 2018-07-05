package de.pheromir.discordmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;

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
		if (!Main.djs.contains(e.getAuthor().getId()) && !e.isOwner()) {
			e.reply("Du musst ein DJ sein um die Lautstärke anpassen zu können");
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
