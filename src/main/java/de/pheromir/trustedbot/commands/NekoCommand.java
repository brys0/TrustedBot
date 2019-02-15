package de.pheromir.trustedbot.commands;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class NekoCommand extends RandomImageCommand {

	public NekoCommand() {
		this.BASE_URL = "https://nekos.life/api/v2/img/neko";
		this.jsonKey = "neko";
		this.name = "neko";
		this.aliases = new String[] { "catgirl" };
		this.help = "Shows a random neko picture.";
	}

}
