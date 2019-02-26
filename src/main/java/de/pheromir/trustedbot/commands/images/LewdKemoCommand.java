package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class LewdKemoCommand extends RandomImageCommand {

	public LewdKemoCommand() {
		this.name = "lewdkemo";
		this.help = "Shows a random lewd kemonomimi picture.";
		this.BASE_URL = "https://nekos.life/api/v2/img/lewdkemo";
		this.jsonKey = "url";
		this.creditsCost = 5L;
		this.nsfw = true;
	}
}
