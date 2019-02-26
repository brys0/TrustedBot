package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class LewdYuriCommand extends RandomImageCommand {

	public LewdYuriCommand() {
		this.name = "lewdyuri";
		this.help = "Shows a random lewd yuri picture.";
		this.BASE_URL = "https://nekos.life/api/v2/img/yuri";
		this.jsonKey = "url";
		this.creditsCost = 5L;
		this.nsfw = true;
	}
}
