package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class LoliCommand extends RandomImageCommand {

	public LoliCommand() {
		this.name = "loli";
		this.help = "Shows a random loli picture. At least most of the time..";
		this.BASE_URL = "https://nekos.life/api/v2/img/ero";
		this.jsonKey = "url";
		this.nsfw = true;
	}
}
