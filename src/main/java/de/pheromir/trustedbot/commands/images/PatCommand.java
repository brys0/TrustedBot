package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class PatCommand extends RandomImageCommand {

	public PatCommand() {
		this.BASE_URL = "https://nekos.life/api/v2/img/pat";
		this.jsonKey = "url";
		this.name = "pat";
		this.help = "Shows a random pat gif.";
	}

}
