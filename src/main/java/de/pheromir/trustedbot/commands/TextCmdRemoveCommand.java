package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;

public class TextCmdRemoveCommand extends TrustedCommand {

	public TextCmdRemoveCommand() {
		this.name = "textcmdremove";
		this.help = "Remove a custom text-command.";
		this.arguments = "<command>";
		this.guildOnly = true;
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.category = new Category("Custom Commands");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (args.length == 0) {
			e.reply(usage);
			return false;
		}

		String name = args[0].toLowerCase();
		if (!gc.getCustomCommands().containsKey(name)) {
			e.reply("There is no text-command with the specified name.");
			return false;
		}
		gc.removeCustomCommand(name);
		e.reply("The text-command `" + name + "` has been removed.");
		return true;
	}
}