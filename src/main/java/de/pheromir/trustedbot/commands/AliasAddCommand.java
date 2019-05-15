package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;

public class AliasAddCommand extends TrustedCommand {

	public AliasAddCommand() {
		this.name = "aliasadd";
		this.help = "Create an alias for a command";
		this.arguments = "<alias> <command> <arguments>";
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.guildOnly = true;
		this.category = new Category("Command Aliases");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (args.length < 2) {
			e.reply(usage);
			return false;
		}

		String name = args[0].toLowerCase();
		if (Main.commandClient.getCommands().stream().anyMatch(c -> c.isCommandFor(name))
				|| gc.getCustomCommands().containsKey(name)) {
			e.reply("There is already a command with that name.");
			return false;
		}
		String cmd = args[1].toLowerCase();
		String arguments = "";
		if (args.length >= 2) {
			StringBuilder sb = new StringBuilder();
			for (int i = 2; i < args.length; i++) {
				sb.append(args[i] + " ");
			}
			arguments = sb.toString().trim();
		}
		if (gc.getAliasCommands().containsKey(name)) {
			e.reply("The alias `" + name + "` has been replaced.");
		} else {
			e.reply("The alias `" + name + "` has been created.");
		}
		gc.addAliasCommand(name, cmd, arguments);
		return true;
	}
}