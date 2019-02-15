package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;

public class VolumeCommand extends TrustedCommand {

	public VolumeCommand() {
		this.name = "volume";
		this.arguments = "<1-100>";
		this.aliases = new String[] { "vol" };
		this.help = "Set the playback volume. (currently only for selected users available)";
		this.guildOnly = true;
		this.category = new Category("Music");
	}

	@Override
	protected void exec(CommandEvent e) {
		if (e.getArgs().isEmpty()) {
			e.reply("Current volume: " + Main.getGuildConfig(e.getGuild()).player.getVolume());
			return;
		}
		if (!Main.getExtraUsers().contains(e.getAuthor().getIdLong())) {
			e.reply("For performance reasons, this command is only available for selected users. Sorry.\n"
					+ "You can control the volume in your discord-client by rightclicking me.");
			return;
		}
		if(!Main.getGuildConfig(e.getGuild()).getDJs().contains(e.getAuthor().getIdLong())) {
			e.reply("You need DJ permissions to use this command.");
			return;
		}

		int vol = 100;
		try {
			vol = Integer.parseInt(e.getArgs());
			if (vol < 1 || vol > 100) {
				throw new NumberFormatException("Volume out of range. (1-100)");
			}
		} catch (NumberFormatException ex) {
			e.reply("Invalid value. Please specify a value between 1-100.");
			return;
		}
		Main.getGuildConfig(e.getGuild()).setVolume(vol);
		e.reactSuccess();

	}

}
