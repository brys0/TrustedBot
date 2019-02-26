package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class KemoCommand extends RandomImageCommand {

	public KemoCommand() {
		this.name = "kemo";
		this.help = "Shows a random kemonomimi picture.";
		this.BASE_URL = "https://nekos.life/api/v2/img/kemonomimi";
		this.jsonKey = "url";
	}
}
