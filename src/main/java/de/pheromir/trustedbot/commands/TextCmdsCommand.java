package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;

public class TextCmdsCommand extends TrustedCommand {

	public TextCmdsCommand() {
		this.name = "textcmds";
		this.help = "List all custom text-commands for the current guild.";
		this.guildOnly = true;
		this.category = new Category("Custom Commands");
	}

	@Override
	protected void exec(CommandEvent e) {
		if(Main.getGuildConfig(e.getGuild()).getCustomCommands().size() < 1) {
			e.reply("There are currently no custom commands.");
			return;
		}
		String cmds = "There are currently the following text-commands: ";
		StringBuilder sb = new StringBuilder();
		for (String str : Main.getGuildConfig(e.getGuild()).getCustomCommands().keySet()) {
			sb.append("`" + str + "`, ");
		}
		e.reply(cmds + sb.toString().substring(0, sb.length() - 2));
	}
}