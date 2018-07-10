package de.pheromir.discordmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class R6ClearCommand extends Command {

	public R6ClearCommand() {
		this.name = "r6clear";
		this.help = "Rainbow Six Statistik-Cache leeren";
		this.guildOnly = false;
		this.ownerCommand = true;
	}

	@Override
	protected void execute(CommandEvent e) {
		R6Command.statsCache.clear();
		e.reply("Der Statistik-Zwischenspeicher wurde geleert.");
	}

}
