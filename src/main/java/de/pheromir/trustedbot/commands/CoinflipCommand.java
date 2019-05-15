package de.pheromir.trustedbot.commands;

import java.util.Random;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;

public class CoinflipCommand extends TrustedCommand {

	public CoinflipCommand() {
		this.name = "cf";
		this.aliases = new String[] { "coinflip" };
		this.arguments = "<heads|tails> <stake>";
		this.help = "Flip a coin to win credits.";
		this.guildOnly = true;
		this.category = new Category("Minigames and Gambling");
		this.cooldown = 5;
		this.cooldownScope = CooldownScope.USER_GUILD;
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (args.length != 2 || !args[0].toLowerCase().matches("^(heads|tails)$")) {
			e.reply(usage);
			return false;
		}

		long stake;
		try {
			stake = Long.parseLong(args[1]);
		} catch (NumberFormatException e1) {
			e.reply("Please enter a valid number.");
			return false;
		}

		if (stake > gc.getUserCredits(e.getAuthor().getIdLong())) {
			e.reply("You don't have enough credits.");
			return false;
		}

		// heads = true, tails = false
		boolean chosenSide = args[0].equalsIgnoreCase("heads") ? true : false;
		boolean random = new Random().nextBoolean();
		if (random == chosenSide) {
			long wonCredits = (long) Math.ceil((stake * 0.5));
			e.reply((random ? "Heads!" : "Tails!") + " You've won, " + e.getAuthor().getAsMention() + "! Your prize: "
					+ wonCredits + " Credits.");
			gc.setUserCredits(e.getAuthor().getIdLong(), gc.getUserCredits(e.getAuthor().getIdLong()) + wonCredits);
		} else {
			e.reply((random ? "Heads!" : "Tails!") + " You've lost your stake, " + e.getAuthor().getAsMention() + ".");
			gc.setUserCredits(e.getAuthor().getIdLong(), gc.getUserCredits(e.getAuthor().getIdLong()) - stake);
		}

		return true;
	}

}
