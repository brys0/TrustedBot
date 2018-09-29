package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import net.dv8tion.jda.core.Permission;

public class TextCmdAddCommand extends Command {

	public TextCmdAddCommand() {
		this.name = "textcmdadd";
		this.help = "Einen Textbefehl erstellen";
		this.arguments = "<command> <antwort>";
		this.guildOnly = true;
	}

	@Override
	protected void execute(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		if (!e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
			e.reply("Du hast keine Rechte für diesen Befehl.");
			return;
		}

		if (args.length < 2) {
			e.reply("Syntaxfehler. Verwendung: !" + name + " <Command> <Argumente>");
			return;
		}

		String name = args[0].toLowerCase();
		if (Main.commandClient.getCommands().stream().anyMatch(c -> c.isCommandFor(name))
				|| Main.getGuildConfig(e.getGuild()).getAliasCommands().containsKey(name)) {
			e.reply("Es existiert bereits ein Befehl mit diesem Namen.");
			return;
		}
		String arguments = "";
		if (args.length > 2) {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				sb.append(args[i] + " ");
			}
			arguments = sb.toString().trim();
		}
		if (Main.getGuildConfig(e.getGuild()).getCustomCommands().containsKey(name)) {
			e.reply("Der Textbefehl `" + name + "` wurde ersetzt.");
		} else {
			e.reply("Der Textbefehl `" + name + "` wurde erstellt.");
		}
		Main.getGuildConfig(e.getGuild()).addCustomCommand(name, arguments);
	}
}