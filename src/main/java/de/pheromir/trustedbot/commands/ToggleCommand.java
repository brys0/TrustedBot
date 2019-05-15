package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
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
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (args.length == 0) {
			if (gc.getDisabledCommands().isEmpty()) {
				e.reply("There is currently no command disabled.");
				return false;
			} else {
				StringBuilder sb = new StringBuilder();
				for (String str : gc.getDisabledCommands()) {
					sb.append("`" + str + "`, ");
				}
				String msg = sb.substring(0, sb.length() - 2);
				e.reply("These commands are currently disabled: " + msg);
				return false;
			}
		}
		if (args.length != 1) {
			e.reply(usage);
			return false;
		} else {
			Command c = Main.commandClient.getCommands().stream().filter(cmd -> cmd.isCommandFor(args[0])).findAny().orElse(null);
			if (c == null && !gc.getCustomCommands().containsKey(args[0].toLowerCase())
					&& !gc.getAliasCommands().containsKey(args[0].toLowerCase())) {
				e.reply("You can't disable a non-existant command.");
				return false;
			}
			if (((c == null) ? args[0] : c.getName()).equalsIgnoreCase("toggle") || (c != null && c.isOwnerCommand())) {
				e.reply("You can't disable this command.");
				return false;
			}
			if (gc.getDisabledCommands().contains(((c == null) ? args[0] : c.getName()))) {
				gc.enableCommand(((c == null) ? args[0] : c.getName()));
				e.reply("The command `" + ((c == null) ? args[0] : c.getName()) + "` is now enabled.");
				return true;
			} else {
				gc.disableCommand((c == null) ? args[0] : c.getName());
				e.reply("The command `" + ((c == null) ? args[0] : c.getName()) + "` is now disabled.");
				return true;
			}
		}
	}

}
