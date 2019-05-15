package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;

public class PrefixCommand extends TrustedCommand {

	public PrefixCommand() {
		this.name = "prefix";
		this.aliases = new String[] { "präfix" };
		this.help = "Edit the prefix of the commands for this guild.";
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.guildOnly = true;
		this.category = new Category("Settings");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (args.length == 0) {
			e.reply("Current prefix: " + gc.getPrefix());
			return false;
		}

		gc.setPrefix(e.getArgs());
		e.reply("The prefix has been set to `" + e.getArgs() + "`.");
		return true;
	}

}
