package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import net.dv8tion.jda.core.entities.ChannelType;

public class TextCmdsCommand extends Command {

	public TextCmdsCommand() {
		this.name = "textcmds";
		this.help = "List all custom text-commands for the current guild.";
		this.guildOnly = true;
		this.category = new Category("Custom Commands");
	}

	@Override
	protected void execute(CommandEvent e) {
		if(e.getChannelType() == ChannelType.TEXT && Main.getGuildConfig(e.getGuild()).isCommandDisabled(this.name)) {
			e.reply(Main.COMMAND_DISABLED);
			return;
		}
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