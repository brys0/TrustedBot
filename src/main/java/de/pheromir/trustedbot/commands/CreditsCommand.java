package de.pheromir.trustedbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

public class CreditsCommand extends TrustedCommand {

	public CreditsCommand() {
		this.name = "credits";
		this.help = "Show your Credits";
		this.guildOnly = true;
		this.category = new Category("Money");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (!e.getArgs().isEmpty() && e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
			Pattern p = Pattern.compile("(\\d+)");
			Matcher m = p.matcher(e.getArgs());
			if (!m.find()) {
				e.reply("The specified user couldn't be found.");
				return false;
			}
			String id = m.group(1);
			Member mem = e.getGuild().getMemberById(id);
			if (mem == null) {
				e.reply("The specified user couldn't be found.");
				return false;
			}
			e.reply(mem.getEffectiveName() + " has **" + gc.getUserCredits(mem.getUser().getIdLong()) + "** credits.");
			return true;
		} else {
			e.reply(e.getMember().getEffectiveName() + ", you have **"
					+ gc.getUserCredits(e.getMember().getUser().getIdLong()) + "** credits.");
			return true;
		}
	}

}
