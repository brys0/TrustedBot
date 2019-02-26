package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class TickleCommand extends RandomImageCommand {

	public TickleCommand() {
		this.BASE_URL = "https://nekos.life/api/v2/img/tickle";
		this.jsonKey = "url";
		this.name = "tickle";
		this.help = "Shows a random tickle gif.";
	}

}
