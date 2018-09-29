package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;

public class PrefixCommand extends Command {

	public PrefixCommand() {
		this.name = "prefix";
		this.aliases = new String[] { "präfix" };
		this.help = "Präfix anpassen";
		this.guildOnly = true;
	}

	@Override
	protected void execute(CommandEvent e) {
		if (e.getArgs().isEmpty()) {
			e.reply("Derzeitiges Präfix: " + Main.getGuildConfig(e.getGuild()).getPrefix());
			return;
		}

		Main.getGuildConfig(e.getGuild()).setPrefix(e.getArgs());
		e.reply("Das Präfix wurde auf `" + e.getArgs() + "` gesetzt.");

	}

}
