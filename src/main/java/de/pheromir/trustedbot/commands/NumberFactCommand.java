package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.pheromir.trustedbot.commands.base.TrustedCommand;
import net.dv8tion.jda.core.Permission;

public class NumberFactCommand extends TrustedCommand {

	private final String BASE_URL = "http://numbersapi.com/{n}";

	public NumberFactCommand() {
		this.name = "numberfact";
		this.aliases = new String[] { "nf" };
		this.botPermissions = new Permission[] { Permission.MESSAGE_WRITE };
		this.help = "Shows a fact about the given number.";
		this.guildOnly = false;
		this.category = new Category("Miscellaneous");
		this.cooldown = 30;
		this.cooldownScope = CooldownScope.USER_GUILD;
	}

	@Override
	protected boolean exec(CommandEvent e) {
		int n;
		try {
			n = Integer.parseInt(e.getArgs());
		} catch  (NumberFormatException e1) {
			e.reply("Invalid number.");
			return false;
		}
		Unirest.get(BASE_URL).routeParam("n", n+"").asStringAsync(new Callback<String>() {

			@Override
			public void cancelled() {
				e.reply("An error occured while getting your fact.");
			}

			@Override
			public void completed(HttpResponse<String> arg0) {
				if(arg0.getBody().contains("is a number for which we're missing a fact")) {
					e.reply("There is currently no fact for the number "+n+". We're sorry to disappoint you.");
				} else {
					e.reply(arg0.getBody());
				}
			}

			@Override
			public void failed(UnirestException arg0) {
				e.reply("An error occured while getting your fact.");
			}
			
		});
		return true;
	}

}
