package de.pheromir.trustedbot.commands;

import java.util.Random;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;

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
	protected boolean exec(CommandEvent e) {
		if (getArgs().length != 2
				|| (!getArgs()[0].equalsIgnoreCase("heads") && !getArgs()[0].equalsIgnoreCase("tails"))) {
			e.reply("Invalid syntax. Use " + Main.getGuildConfig(e.getGuild()).getPrefix()
					+ "cf <heads|tails> <stake>");
			return false;
		}

		long stake;
		try {
			stake = Long.parseLong(getArgs()[1]);
		} catch (NumberFormatException e1) {
			e.reply("Please enter a valid number.");
			return false;
		}

		if (stake > Main.getGuildConfig(e.getGuild()).getUserCredits(e.getAuthor().getIdLong())) {
			e.reply("You don't have enough credits.");
			return false;
		}

		// heads = true, tails = false
		boolean chosenSide = getArgs()[0].equalsIgnoreCase("heads") ? true : false;
		boolean random = new Random().nextBoolean();
		if (random == chosenSide) {
			long wonCredits = (long) Math.ceil((stake * 0.5));
			e.reply((random ? "Heads!" : "Tails!") + " You've won, " + e.getAuthor().getAsMention() + "! Your prize: "
					+ wonCredits + " Credits.");
			Main.getGuildConfig(e.getGuild()).setUserCredits(e.getAuthor().getIdLong(), Main.getGuildConfig(e.getGuild()).getUserCredits(e.getAuthor().getIdLong())
					+ wonCredits);
		} else {
			e.reply((random ? "Heads!" : "Tails!") + " You've lost your stake, " + e.getAuthor().getAsMention() + ".");
			Main.getGuildConfig(e.getGuild()).setUserCredits(e.getAuthor().getIdLong(), Main.getGuildConfig(e.getGuild()).getUserCredits(e.getAuthor().getIdLong())
					- stake);
		}

		return true;
	}

}
