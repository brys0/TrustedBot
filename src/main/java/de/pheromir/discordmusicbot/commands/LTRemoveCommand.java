package de.pheromir.discordmusicbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;
import net.dv8tion.jda.core.entities.Member;

public class LTRemoveCommand extends Command {

	public LTRemoveCommand() {
		this.name = "ltremove";
		this.help = "Person die Erlaubnis entziehen, längere Titel abzuspielen.";
		this.arguments = "<Person>";
		this.guildOnly = true;
		this.ownerCommand = true;
	}

	@Override
	protected void execute(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		for (String arg : args) {
			Pattern p = Pattern.compile("(\\d+)");
			Matcher m = p.matcher(arg);
			if (m.find()) {
				String id = m.group(1);
				Member mem = e.getGuild().getMemberById(id);
				if (mem == null) {
					e.reply("Es konnte kein entsprechender Nutzer gefunden werden.");
					continue;
				}
				if (!Main.getGuildConfig(e.getGuild()).getLongTitlesUsers().contains(Long.parseLong(id))) {
					e.reply(mem.getAsMention() + " hat gar keine Erlaubnis, längere Titel abzuspielen.");
					continue;
				}
				if (mem != null) {
					e.reply(mem.getAsMention() + " hat nun keine Erlaubnis mehr, längere Titel abzuspielen.");
					Main.getGuildConfig(e.getGuild()).removeLongTitlesUser(Long.parseLong(id));
					continue;
				}
			}
			e.reply("Es konnte kein entsprechender Nutzer gefunden werden.");
			continue;
		}
	}

}
