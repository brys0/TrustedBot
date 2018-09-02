package de.pheromir.discordmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class GCCommand extends Command {

	public GCCommand() {
		this.name = "gc";
		this.help = "Garbage Collector ausführen";
		this.guildOnly = false;
		this.ownerCommand = true;
	}

	@Override
	protected void execute(CommandEvent e) {
		System.gc();
		e.reactSuccess();
	}

}
