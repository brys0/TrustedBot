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
		this.category = new Category("Music");
	}

	@Override
	protected void execute(CommandEvent e) {
		if (e.getAuthor().isBot())
			return;
		if (e.getArgs().isEmpty()) {
			e.reply("Derzeitige Lautstärke: " + Main.getGuildConfig(e.getGuild()).player.getVolume());
			return;
		}
		if (!Main.getExtraUsers().contains(e.getAuthor().getIdLong())) {
			e.reply("Aus Performancegründen ist dieser Befehl nur für ausgewählte User freigeschaltet. Sorry.");
			return;
		}
		
		int vol = 100;
		try {
			vol = Integer.parseInt(e.getArgs());
			if(vol < 1 || vol > 100) {
				throw new NumberFormatException("Lautstärke außerhalb des Bereiches 1-100.");
			}
		} catch (NumberFormatException ex) {
			e.reply("Ungültiger Wert. Bitte einen Wert von 1-100 angeben.");
			return;
		}
		Main.getGuildConfig(e.getGuild()).setVolume(vol);
		e.reactSuccess();

	}

}
