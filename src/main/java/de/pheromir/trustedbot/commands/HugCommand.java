package de.pheromir.trustedbot.commands;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class HugCommand extends RandomImageCommand {

	public HugCommand() {
		this.BASE_URL = "https://nekos.life/api/v2/img/hug";
		this.jsonKey = "url";
		this.name = "hug";
		this.help = "Shows a random hug gif.";
	}

}
