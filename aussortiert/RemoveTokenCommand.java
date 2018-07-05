package de.pheromir.discordmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;

public class RemoveTokenCommand extends Command {

	public RemoveTokenCommand() {
		this.name = "removetoken";
		this.arguments = "<ID>";
		this.help = "Einen vorhandenen Token entfernen. ID über den Befehl `tokens` einsehbar";
		this.guildOnly = false;
	}

	@Override
	protected void execute(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		if (args.length != 1) {
			e.reply("**Verwendung**:\n" + "`!removetoken <ID>`\n" + "*Die ID ist über !tokens einsehbar*");
			return;
		}
		int id = -1;
		try {
			id = Integer.parseInt(args[0]);
		} catch (NumberFormatException et) {
			e.reply("**Fehler**: `Ungültige ID.`");
			return;
		}

		if (!Main.roleTokenNames.containsKey(id) && !Main.roleTokenTokens.containsKey(id)) {
			e.reply("**Fehler**: `Der Token mit der ID " + id + " konnte nicht gefunden werden.`");
			return;
		}
		Main.roleTokenNames.remove(id);
		Main.roleTokenTokens.remove(id);
		Main.saveRoleTokens();
		e.reactSuccess();
		return;
	}

	// @Command(aliases = {"!removetoken"}, description = "Role-Token
	// entfernen", requiredPermissions = "bot.removetoken")
	// public String onCommand(String command, String[] args, TextChannel
	// channel, ServerTextChannel sc, DiscordApi api, User user) {

	// }
}
