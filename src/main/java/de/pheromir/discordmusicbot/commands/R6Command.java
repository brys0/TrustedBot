package de.pheromir.discordmusicbot.commands;

import java.util.HashMap;
import java.util.Timer;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.helper.R6StatsSender;
import de.pheromir.discordmusicbot.helper.RainbowSixStats;
import net.dv8tion.jda.core.Permission;

public class R6Command extends Command {

	public static HashMap<String, RainbowSixStats> statsCache = new HashMap<>();

	public R6Command() {
		this.name = "r6";
		this.botPermissions = new Permission[] { Permission.MESSAGE_WRITE };
		this.help = "Zeige die Stats von Rainbow Six Spielern auf PC an";
		this.guildOnly = false;
	}

	@Override
	protected void execute(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		if (args.length != 1) {
			e.reply("Syntaxfehler. Verwendung: !r6 <UPlay-Name>");
			return;
		}

		Timer timer = new Timer();
		timer.schedule(new R6StatsSender(e.getChannel(), e.getArgs()), 100);
	}

}
