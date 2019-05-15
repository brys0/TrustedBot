package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;

public class TextCmdAddCommand extends TrustedCommand {

	public TextCmdAddCommand() {
		this.name = "textcmdadd";
		this.help = "Create a custom text-command.";
		this.arguments = "<command> <response>";
		this.guildOnly = true;
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.category = new Category("Custom Commands");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (args.length < 2) {
			e.reply(usage);
			return false;
		}

		String name = args[0].toLowerCase();
		if (Main.commandClient.getCommands().stream().anyMatch(c -> c.isCommandFor(name))
				|| gc.getAliasCommands().containsKey(name)) {
			e.reply("There is already a command with that name.");
			return false;
		}
		String arguments = "";
		if (args.length >= 2) {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				sb.append(args[i] + " ");
			}
			arguments = sb.toString().trim();
		}
		if (gc.getCustomCommands().containsKey(name)) {
			e.reply("The text-command `" + name + "` has been replaced.");
		} else {
			e.reply("The text-command `" + name + "` has been created.");
		}
		gc.addCustomCommand(name, arguments);
		return true;
	}
}