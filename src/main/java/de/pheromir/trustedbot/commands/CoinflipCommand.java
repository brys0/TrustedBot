package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;

public class CoinflipCommand extends TrustedCommand {
	
	public CoinflipCommand() {
		this.name = "cf";
		this.aliases = new String[] {"coinflip"};
		this.arguments = "<heads|tails> <stake>";
		this.help = "Flip a coin to win credits.";
		this.guildOnly = true;
	}

	@Override
	protected boolean exec(CommandEvent e) {
		if(getArgs().length != 2 || (!getArgs()[0].equalsIgnoreCase("heads") && !getArgs()[0].equalsIgnoreCase("tails"))) {
			e.reply("Invalid syntax. Use "+Main.getGuildConfig(e.getGuild()).getPrefix()+"cf <heads|tails> <stake>");
			return false;
		}
		
		return true;
	}

	
	
}
