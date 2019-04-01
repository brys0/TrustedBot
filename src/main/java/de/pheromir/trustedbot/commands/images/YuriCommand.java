package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class YuriCommand extends RandomImageCommand {

	public YuriCommand() {
		this.name = "yuri";
		this.help = "Shows a random ero yuri picture.";
		this.BASE_URL = "https://nekos.life/api/v2/img/eroyuri";
		this.jsonKey = "url";
		this.nsfw = true;
	}
}
