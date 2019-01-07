package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;

public class AliasRemoveCommand extends Command {

	public AliasRemoveCommand() {
		this.name = "aliasremove";
		this.help = "Remove an alias.";
		this.arguments = "<alias>";
		this.guildOnly = true;
		this.userPermissions = new Permission[] {Permission.ADMINISTRATOR};
		this.category = new Category("Command Aliases");
	}

	@Override
	protected void execute(CommandEvent e) {
		if(e.getChannelType() == ChannelType.TEXT && Main.getGuildConfig(e.getGuild()).isCommandDisabled(this.name)) {
			e.reply(Main.COMMAND_DISABLED);
			return;
		}
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];
		
		if (args.length == 0) {
			e.reply("Syntaxerror. Usage: !" + name + " <alias>");
			return;
		}

		String name = args[0].toLowerCase();
		if (!Main.getGuildConfig(e.getGuild()).getAliasCommands().containsKey(name)) {
			e.reply("There is no alias with the specified name.");
			return;
		}
		Main.getGuildConfig(e.getGuild()).removeAliasCommand(name);
		e.reply("The alias `" + name + "` has been removed.");
	}
}