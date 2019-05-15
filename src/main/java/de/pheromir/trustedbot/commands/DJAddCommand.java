package de.pheromir.trustedbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

public class DJAddCommand extends TrustedCommand {

	public DJAddCommand() {
		this.name = "djadd";
		this.help = "Assign DJ permissions to the specified user.";
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.arguments = "<User-Mention>";
		this.guildOnly = true;
		this.category = new Category("Music");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
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
				if (gc.getDJs().contains(Long.parseLong(id))) {
					e.reply(mem.getAsMention() + " is already a DJ.");
					continue;
				} else {
					e.reply(mem.getAsMention() + " is now a DJ.");
					gc.addDJ(Long.parseLong(id));
					continue;
				}
			} else {
				e.reply("The specified user couldn't be found.");
				continue;
			}
		}
		return true;
	}
}
