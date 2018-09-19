package de.pheromir.discordmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;
import net.dv8tion.jda.core.Permission;

public class AliasAddCommand extends Command {

	public AliasAddCommand() {
		this.name = "aliasadd";
		this.help = "Einen Alias für einen Befehl erstellen";
		this.arguments = "<alias> <command> <arguments>";
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
			e.reply("Syntaxfehler. Verwendung: !" + name + " <Alias> <Command> [Argumente]");
			return;
		}

		String name = args[0].toLowerCase();
		if (Main.commandClient.getCommands().stream().anyMatch(c -> c.isCommandFor(name))
				|| Main.getGuildConfig(e.getGuild()).getCustomCommands().containsKey(name)) {
			e.reply("Es existiert bereits ein Befehl mit diesem Namen.");
			return;
		}
		String cmd = args[1].toLowerCase();
		String arguments = "";
		if (args.length > 2) {
			StringBuilder sb = new StringBuilder();
			for (int i = 2; i < args.length; i++) {
				sb.append(args[i] + " ");
			}
			arguments = sb.toString().trim();
		}
		if (Main.getGuildConfig(e.getGuild()).getAliasCommands().containsKey(name)) {
			e.reply("Der Alias `" + name + "` wurde ersetzt.");
		} else {
			e.reply("Der Alias `" + name + "` wurde erstellt.");
		}
		Main.getGuildConfig(e.getGuild()).addAliasCommand(name, cmd, arguments);
	}
}