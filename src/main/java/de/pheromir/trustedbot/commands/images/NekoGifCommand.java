package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class NekoGifCommand extends RandomImageCommand {

	public NekoGifCommand() {
		this.BASE_URL = "https://nekos.life/api/v2/img/ngif";
		this.jsonKey = "url";
		this.name = "nekogif";
		this.help = "Shows a random neko gif.";
	}

}
