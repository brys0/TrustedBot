package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;

public class NumberFactCommand extends TrustedCommand {

	private final String BASE_URL = "http://numbersapi.com/{n}";

	public NumberFactCommand() {
		this.name = "numberfact";
		this.aliases = new String[] { "nf" };
		this.arguments = "<Integer>";
		this.botPermissions = new Permission[] { Permission.MESSAGE_WRITE };
		this.help = "Shows a fact about the given number.";
		this.guildOnly = false;
		this.category = new Category("Miscellaneous");
		this.cooldown = 10;
		this.cooldownScope = CooldownScope.USER_GUILD;
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (args.length == 0) {
			e.reply(usage);
			return false;
		}
		int n;
		try {
			n = Integer.parseInt(e.getArgs());
			if (n < 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e1) {
			e.reply(usage);
			return false;
		}
		Unirest.get(BASE_URL).routeParam("n", n + "").asStringAsync(new Callback<String>() {

			@Override
			public void cancelled() {
				e.reply("An error occured while getting your fact.");
			}

			@Override
			public void completed(HttpResponse<String> response) {
				if (response.getStatus() != 200) {
					e.reply("An error occurred while getting your fact.");
					Main.LOG.error("NumberFact received a HTTP Code " + response.getStatus());
					return;
				}
				e.reply(response.getBody());
			}

			@Override
			public void failed(UnirestException arg0) {
				e.reply("An error occured while getting your fact.");
			}

		});
		return true;
	}

}
