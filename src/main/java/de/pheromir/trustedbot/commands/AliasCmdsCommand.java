package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;

public class AliasCmdsCommand extends TrustedCommand {

	public AliasCmdsCommand() {
		this.name = "aliases";
		this.help = "List all aliases for the current guild.";
		this.guildOnly = true;
		this.category = new Category("Command Aliases");
	}

	@Override
	protected boolean exec(CommandEvent e) {
		if(Main.getGuildConfig(e.getGuild()).getAliasCommands().size() < 1) {
			e.reply("There are currently no aliases.");
			return false;
		}

		String cmds = "There are currently the following aliases: ";
		StringBuilder sb = new StringBuilder();
		for (String str : Main.getGuildConfig(e.getGuild()).getAliasCommands().keySet()) {
			sb.append("`" + str + "`, ");
		}
		e.reply(cmds + sb.toString().substring(0, sb.length() - 2));
		return true;
	}
}