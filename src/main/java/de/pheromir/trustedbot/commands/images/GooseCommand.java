package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class GooseCommand extends RandomImageCommand {

	public GooseCommand() {
		this.BASE_URL = "https://nekos.life/api/v2/img/goose";
		this.jsonKey = "url";
		this.name = "goose";
		this.help = "Shows a random goose image.";
	}

}
