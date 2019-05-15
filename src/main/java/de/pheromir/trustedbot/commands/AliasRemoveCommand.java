package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;

public class AliasRemoveCommand extends TrustedCommand {

	public AliasRemoveCommand() {
		this.name = "aliasremove";
		this.help = "Remove an alias.";
		this.arguments = "<alias>";
		this.guildOnly = true;
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.category = new Category("Command Aliases");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (args.length == 0) {
			e.reply(usage);
			return false;
		}

		String name = args[0].toLowerCase();
		if (!gc.getAliasCommands().containsKey(name)) {
			e.reply("There is no alias with the specified name.");
			return false;
		}
		gc.removeAliasCommand(name);
		e.reply("The alias `" + name + "` has been removed.");
		return true;
	}
}