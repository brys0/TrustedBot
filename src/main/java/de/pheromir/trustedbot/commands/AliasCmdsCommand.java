package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import net.dv8tion.jda.core.entities.ChannelType;

public class AliasCmdsCommand extends Command {

	public AliasCmdsCommand() {
		this.name = "aliases";
		this.help = "List all aliases for the current guild.";
		this.guildOnly = true;
		this.category = new Category("Command Aliases");
	}

	@Override
	protected void execute(CommandEvent e) {
		if(e.getChannelType() == ChannelType.TEXT && Main.getGuildConfig(e.getGuild()).isCommandDisabled(this.name)) {
			e.reply(Main.COMMAND_DISABLED);
			return;
		}
		if(Main.getGuildConfig(e.getGuild()).getAliasCommands().size() < 1) {
			e.reply("There are currently no aliases.");
			return;
		}

		String cmds = "There are currently the following aliases: ";
		StringBuilder sb = new StringBuilder();
		for (String str : Main.getGuildConfig(e.getGuild()).getAliasCommands().keySet()) {
			sb.append("`" + str + "`, ");
		}
		e.reply(cmds + sb.toString().substring(0, sb.length() - 2));
	}
}