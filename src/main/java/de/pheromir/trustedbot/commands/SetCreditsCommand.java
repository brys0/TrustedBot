package de.pheromir.trustedbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

public class SetCreditsCommand extends TrustedCommand {

	public SetCreditsCommand() {
		this.name = "setcredits";
		this.help = "Set the credits of the specified user";
		this.guildOnly = true;
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.category = new Category("Money");
	}

	@Override
	protected void exec(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		Pattern p = Pattern.compile("(\\d+)");
		Matcher m = p.matcher(args[0]);
		if (m.find()) {
			String id = m.group(1);
			Member mem = e.getGuild().getMemberById(id);
			if (mem == null) {
				e.reply("The specified user couldn't be found.");
				return;
			}
			long amount;
			try {
				amount = Long.parseLong(args[1]);
				if (amount < 0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e1) {
				e.reply("Invalid amount.");
				return;
			}
			Main.getGuildConfig(e.getGuild()).setUserCredits(mem.getUser().getIdLong(), amount);
			e.reply(mem.getEffectiveName() + "'s credits have been set to " + amount + ".");

		} else {
			e.reply("The specified user couldn't be found.");
			return;
		}

	}
}
