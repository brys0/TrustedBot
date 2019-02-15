package de.pheromir.trustedbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

public class DJAddCommand extends TrustedCommand {

	public DJAddCommand() {
		this.name = "djadd";
		this.help = "Assign DJ permissions to the specified user.";
		this.userPermissions = new Permission[] {Permission.ADMINISTRATOR};
		this.arguments = "<User-Mention>";
		this.guildOnly = true;
		this.category = new Category("Music");
	}

	@Override
	protected void exec(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		Pattern p = Pattern.compile("(\\d+)");
		for (String arg : args) {
			Matcher m = p.matcher(arg);
			if (m.find()) {
				String id = m.group(1);
				Member mem = e.getGuild().getMemberById(id);
				if (mem == null) {
					e.reply("The specified user couldn't be found.");
					continue;
				}
				if (Main.getGuildConfig(e.getGuild()).getDJs().contains(Long.parseLong(id))) {
					e.reply(mem.getAsMention() + " is already a DJ.");
					continue;
				} else {
					e.reply(mem.getAsMention() + " is now a DJ.");
					Main.getGuildConfig(e.getGuild()).addDJ(Long.parseLong(id));
					continue;
				}
			} else {
				e.reply("The specified user couldn't be found.");
				continue;
			}
		}
	}
}
