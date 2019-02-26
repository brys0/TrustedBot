package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class CatCommand extends RandomImageCommand {

	public CatCommand() {
		this.name = "cat";
		this.help = "Shows a random cat picture.";
		this.BASE_URL = "https://nekos.life/api/v2/img/meow";
		this.jsonKey = "url";
	}
}
