package de.pheromir.discordmusicbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

public class DJRemoveCommand extends Command {

	public DJRemoveCommand() {
		this.name = "djremove";
		this.help = "DJ-Rechte von einer Person entfernen.";
		this.arguments = "<User-Mention>";
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

		if (args.length != 1) {
			e.reply("Syntaxfehler. Verwendung: `!djremove <User-Mention>`");
			return;
		} else {
			Pattern p = Pattern.compile("(\\d+)");
			Matcher m = p.matcher(e.getArgs());
			if (m.find()) {
				String id = m.group(1);
				Member mem = e.getGuild().getMemberById(id);
				if (mem == null) {
					e.reply("Es konnte kein entsprechender Nutzer gefunden werden.");
					return;
				}
				if (!Main.getGuildConfig(e.getGuild()).getDJs().contains(Long.parseLong(id))) {
					e.reply(mem.getAsMention() + " ist gar kein DJ.");
					return;
				}
				if (mem != null) {
					e.reply(mem.getAsMention() + " ist nun kein DJ mehr.");
					Main.getGuildConfig(e.getGuild()).removeDJ(Long.parseLong(id));
					return;
				}
			}
			e.reply("Es konnte kein entsprechender Nutzer gefunden werden.");
			return;
		}
	}

}
