package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class LewdGifCommand extends RandomImageCommand {

	public LewdGifCommand() {
		this.name = "lewdgif";
		this.help = "Shows a random lewd neko gif.";
		this.BASE_URL = "https://nekos.life/api/v2/img/nsfw_neko_gif";
		this.jsonKey = "url";
		this.creditsCost = 5L;
		this.nsfw = true;
	}
}
