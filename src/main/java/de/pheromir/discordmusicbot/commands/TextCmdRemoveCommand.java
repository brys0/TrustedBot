package de.pheromir.discordmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;
import net.dv8tion.jda.core.Permission;

public class TextCmdRemoveCommand extends Command {

	public TextCmdRemoveCommand() {
		this.name = "textcmdremove";
		this.help = "Einen Textbefehl entfernen";
		this.arguments = "<command>";
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

		if (args.length == 0) {
			e.reply("Syntaxfehler. Verwendung: !" + name + " <Befehl>");
			return;
		}

		String name = args[0].toLowerCase();
		if (!Main.getGuildConfig(e.getGuild()).getCustomCommands().containsKey(name)) {
			e.reply("Es existiert kein Textbefehl mit diesem Namen.");
			return;
		}
		Main.getGuildConfig(e.getGuild()).removeCustomCommand(name);
		e.reply("Der Textbefehl `" + name + "` wurde entfernt.");
	}
}