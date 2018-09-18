package de.pheromir.discordmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.Methods;

public class MemoryCommand extends Command {

	public MemoryCommand() {
		this.name = "memory";
		this.aliases = new String[] { "mem" };
		this.help = "Speicherauslastung des Bots anzeigen";
		this.guildOnly = false;
		this.ownerCommand = true;
	}

	@Override
	protected void execute(CommandEvent e) {
		String ans = "Uptime: " + Methods.getTimeString(System.currentTimeMillis() - Main.startMillis) + "\n"
				+ "Maximaler Speicher: " + Runtime.getRuntime().maxMemory() / 1024L / 1024L + " MB\n"
				+ "Reservierter Speicher: " + Runtime.getRuntime().totalMemory() / 1024L / 1024L + " MB\n"
				+ "Freier Speicher: " + Runtime.getRuntime().freeMemory() / 1024L / 1024L + " MB";

		e.reply(ans);
	}

}
