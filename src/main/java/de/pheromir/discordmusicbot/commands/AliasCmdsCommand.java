package de.pheromir.discordmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;

public class AliasCmdsCommand extends Command {

	public AliasCmdsCommand() {
		this.name = "aliases";
		this.help = "Aliase auflisten";
		this.guildOnly = true;
	}

	@Override
	protected void execute(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		String cmds = "Es existieren derzeit folgende Aliase: ";
		StringBuilder sb = new StringBuilder();
		for(String str : Main.getGuildConfig(e.getGuild()).getAliasCommands().keySet()) {
			sb.append("`"+str+"`, ");
		}
		e.reply(cmds + sb.toString().substring(0, sb.length() - 2));
	}
}