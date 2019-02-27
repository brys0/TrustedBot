package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class EroKemoCommand extends RandomImageCommand {

	public EroKemoCommand() {
		this.name = "erokemo";
		this.help = "Shows a random erotic kemonomimi picture.";
		this.BASE_URL = "https://nekos.life/api/v2/img/erokemo";
		this.jsonKey = "url";
		this.creditsCost = 2L;
	}
}
