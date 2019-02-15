package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import net.dv8tion.jda.core.Permission;

public class ToggleCommand extends TrustedCommand {

	public ToggleCommand() {
		this.name = "toggle";
		this.help = "Enable/Disable a command for the guild.";
		this.arguments = "<Command>";
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.guildOnly = true;
		this.category = new Category("Settings");
	}

	@Override
	protected void exec(CommandEvent e) {
		String[] args2 = e.getArgs().split(" ");
		final String[] args;
		if ((args2[0].equals("") || args2[0].isEmpty()) && args2.length == 1)
			args = new String[0];
		else
			args = args2;

		if (args.length == 0) {

			if (Main.getGuildConfig(e.getGuild()).getDisabledCommands().isEmpty()) {
				e.reply("There is currently no command disabled.");
				return;
			} else {
				StringBuilder sb = new StringBuilder();
				for (String str : Main.getGuildConfig(e.getGuild()).getDisabledCommands()) {
					sb.append("`" + str + "`, ");
				}
				String msg = sb.substring(0, sb.length() - 2);
				e.reply("These commands are currently disabled: " + msg);
				return;
			}
		}
		if (args.length != 1) {
			e.reply("Syntaxerror. Usage: `!" + this.name + " [command]`");
			return;
		} else {
			if(args[0].equalsIgnoreCase("toggle")) {
				e.reply("You can't disable this command.");
				return;
			}
			if (Main.getGuildConfig(e.getGuild()).getDisabledCommands().contains(args[0].toLowerCase())) {
				Main.getGuildConfig(e.getGuild()).enableCommand(args[0]);
				e.reply("The command `" + args[0].toLowerCase() + "` is now enabled.");
				return;
			} else {
				Command c = Main.commandClient.getCommands().stream().filter(cmd -> cmd.isCommandFor(args[0])).findAny().orElse(null);
				if (c == null
						&& !Main.getGuildConfig(e.getGuild()).getCustomCommands().containsKey(args[0].toLowerCase())
						&& !Main.getGuildConfig(e.getGuild()).getAliasCommands().containsKey(args[0].toLowerCase())) {
					e.reply("You can't disable a non-existant command.");
					return;
				}
				Main.getGuildConfig(e.getGuild()).disableCommand((c == null) ? args[0] : c.getName());
				e.reply("The command `" + ((c == null) ? args[0] : c.getName()) + "` is now disabled.");
				return;
			}
		}
	}

}
