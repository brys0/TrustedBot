package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;

public class AliasAddCommand extends Command {

	public AliasAddCommand() {
		this.name = "aliasadd";
		this.help = "Create an alias for a command";
		this.arguments = "<alias> <command> <arguments>";
		this.userPermissions = new Permission[] {Permission.ADMINISTRATOR};
		this.guildOnly = true;
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

		if (args.length < 2) {
			e.reply("Syntaxerror. Usage: !" + name + " <alias> <command> [arguments]");
			return;
		}

		String name = args[0].toLowerCase();
		if (Main.commandClient.getCommands().stream().anyMatch(c -> c.isCommandFor(name))
				|| Main.getGuildConfig(e.getGuild()).getCustomCommands().containsKey(name)) {
			e.reply("There is already a command with that name.");
			return;
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
		if (Main.getGuildConfig(e.getGuild()).getAliasCommands().containsKey(name)) {
			e.reply("The alias `" + name + "` has been replaced.");
		} else {
			e.reply("The alias `" + name + "` has been created.");
		}
		Main.getGuildConfig(e.getGuild()).addAliasCommand(name, cmd, arguments);
	}
}