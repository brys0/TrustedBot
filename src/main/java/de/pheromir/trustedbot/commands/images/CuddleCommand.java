package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class CuddleCommand extends RandomImageCommand {

	public CuddleCommand() {
		this.BASE_URL = "https://nekos.life/api/v2/img/cuddle";
		this.jsonKey = "url";
		this.name = "cuddle";
		this.help = "Shows a random cuddle gif.";
	}

}
