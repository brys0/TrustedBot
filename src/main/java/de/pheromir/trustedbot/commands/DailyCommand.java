package de.pheromir.trustedbot.commands;

import java.util.Random;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;

public class DailyCommand extends TrustedCommand {

	public static long reward = 10L;

	public DailyCommand() {
		this.name = "daily";
		this.help = "Claim your daily reward";
		this.guildOnly = true;
		this.category = new Category("Money");

	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (gc.getRewardClaimed(e.getMember().getUser().getIdLong())) {
			e.reply("You already claimed your daily reward. Check back tomorrow!");
			return false;
		}
		e.reply("You claimed your daily reward and got " + reward + " credits.");
		gc.addRewardClaimed(e.getMember().getUser().getIdLong());
		long bonus = 0;
		if (new Random().nextInt(100) < 5) {
			bonus = 20;
			e.reply("BONUS: You're lucky and got a bonus of " + bonus + " credits. (5% chance)");
		}
		gc.setUserCredits(e.getMember().getUser().getIdLong(), gc.getUserCredits(e.getMember().getUser().getIdLong())
				+ reward + bonus);
		return true;
	}

}
