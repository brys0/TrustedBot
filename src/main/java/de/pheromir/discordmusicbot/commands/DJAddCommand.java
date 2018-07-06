package de.pheromir.discordmusicbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

public class DJAddCommand extends Command {

	public DJAddCommand() {
		this.name = "djadd";
		this.help = "DJ-Rechte an eine Person vergeben.";
		this.arguments = "<Person>";
		this.guildOnly = true;
	}

	@Override
	protected void execute(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		if(!e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
			e.reply("Du hast keine Rechte für diesen Befehl.");
			return;
		}
		
		if (args.length != 1) {
			e.reply("Syntaxfehler. Verwendung: `!djadd <User-Mention>`");
			return;
		} else {
			Pattern p = Pattern.compile("(\\d+)");
			Matcher m = p.matcher(e.getArgs());
			if(m.find()) {
				String id = m.group(1);
				Member mem = e.getGuild().getMemberById(id);
				if(Main.getGuildConfig(e.getGuild()).getDJs().contains(Long.parseLong(id))) {
					e.reply(mem.getAsMention()+" ist bereits DJ.");
					return;
				}
				if(mem != null) {
					e.reply(mem.getAsMention()+" ist nun DJ.");
					Main.getGuildConfig(e.getGuild()).addDJ(Long.parseLong(id));
					return;
				}
			}
			e.reply("Es konnte kein entsprechender Nutzer gefunden werden.");
			return;
		}
	}

}
