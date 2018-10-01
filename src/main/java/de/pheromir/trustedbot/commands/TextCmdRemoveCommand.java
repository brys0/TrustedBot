package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import net.dv8tion.jda.core.Permission;

public class TextCmdRemoveCommand extends Command {

	public TextCmdRemoveCommand() {
		this.name = "textcmdremove";
		this.help = "Remove a custom text-command.";
		this.arguments = "<command>";
		this.guildOnly = true;
		this.userPermissions = new Permission[] {Permission.ADMINISTRATOR};
		this.category = new Category("Custom Commands");
	}

	@Override
	protected void execute(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		if (args.length == 0) {
			e.reply("Syntaxerror. Usage: !" + name + " <command>");
			return;
		}

		String name = args[0].toLowerCase();
		if (!Main.getGuildConfig(e.getGuild()).getCustomCommands().containsKey(name)) {
			e.reply("There is no text-command with the specified name.");
			return;
		}
		Main.getGuildConfig(e.getGuild()).removeCustomCommand(name);
		e.reply("The text-command `" + name + "` has been removed.");
	}
}