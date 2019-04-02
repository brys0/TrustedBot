package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import net.dv8tion.jda.core.Permission;

public class AliasRemoveCommand extends TrustedCommand {

	public AliasRemoveCommand() {
		this.name = "aliasremove";
		this.help = "Remove an alias.";
		this.arguments = "<alias>";
		this.guildOnly = true;
		this.userPermissions = new Permission[] {Permission.ADMINISTRATOR};
		this.category = new Category("Command Aliases");
	}

	@Override
	protected boolean exec(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];
		
		if (args.length == 0) {
			e.reply("Syntaxerror. Usage: !" + name + " <alias>");
			return false;
		}

		String name = args[0].toLowerCase();
		if (!Main.getGuildConfig(e.getGuild()).getAliasCommands().containsKey(name)) {
			e.reply("There is no alias with the specified name.");
			return false;
		}
		Main.getGuildConfig(e.getGuild()).removeAliasCommand(name);
		e.reply("The alias `" + name + "` has been removed.");
		return true;
	}
}