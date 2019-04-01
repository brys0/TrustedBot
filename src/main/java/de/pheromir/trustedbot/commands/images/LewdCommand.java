package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class LewdCommand extends RandomImageCommand {

	public LewdCommand() {
		this.name = "lewd";
		this.help = "Shows a random lewd neko picture.";
		this.BASE_URL = "https://nekos.life/api/v2/img/lewd";
		this.jsonKey = "url";
		this.nsfw = true;
	}
}
