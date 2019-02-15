package de.pheromir.trustedbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import net.dv8tion.jda.core.entities.User;

public class ExtraAddCommand extends TrustedCommand {

	public ExtraAddCommand() {
		this.name = "extraadd";
		this.help = "Assign extra permissions to the specified user.";
		this.arguments = "<User-Mention>";
		this.guildOnly = false;
		this.ownerCommand = true;
	}

	@Override
	protected void exec(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		for (String arg : args) {
			Pattern p = Pattern.compile("(\\d+)");
			Matcher m = p.matcher(arg);
			if (m.find()) {
				String id = m.group(1);
				User mem = Main.jda.getUserById(id);
				if (mem == null) {
					e.reply("The specified user couldn't be found.");
					continue;
				}
				if (Main.getExtraUsers().contains(Long.parseLong(id))) {
					e.reply(mem.getAsMention() + " already has extra permissions.");
					continue;
				} else {
					e.reply(mem.getAsMention() + " has been granted extra permissions.");
					Main.addExtraUser(Long.parseLong(id));
				}
				continue;
			} else {
				e.reply("The specified user couldn't be found.");
				continue;
			}
		}
	}
}
