package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;

public class TextCmdsCommand extends TrustedCommand {

	public TextCmdsCommand() {
		this.name = "textcmds";
		this.help = "List all custom text-commands for the current guild.";
		this.guildOnly = true;
		this.category = new Category("Custom Commands");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (gc.getCustomCommands().size() < 1) {
			e.reply("There are currently no custom commands.");
			return false;
		}
		String cmds = "There are currently the following text-commands: ";
		StringBuilder sb = new StringBuilder();
		for (String str : gc.getCustomCommands().keySet()) {
			sb.append("`" + str + "`, ");
		}
		e.reply(cmds + sb.toString().substring(0, sb.length() - 2));
		return true;
	}
}