package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.commands.base.TrustedCommand;

public class StatsCommand extends TrustedCommand {

	public StatsCommand() {
		this.name = "stats";
		this.help = "Shows current statistics of the bot";
		this.guildOnly = false;
		this.ownerCommand = true;
	}

	@Override
	protected boolean exec(CommandEvent e) {
		String ans = "Uptime: "
				+ Methods.getTimeString(System.currentTimeMillis() - Main.startMillis) + "\n"
				+ "Exceptions (WARN or higher): " + Main.exceptionAmount + "\n" 
				+ "Max Memory: "
				+ Runtime.getRuntime().maxMemory() / 1024L / 1024L + " MiB\n" + "Reserved Memory: "
				+ Runtime.getRuntime().totalMemory() / 1024L / 1024L + " MiB\n" + "Free Memory: "
				+ Runtime.getRuntime().freeMemory() / 1024L / 1024L + " MiB";
		e.reply(ans);
		return true;
	}

}
