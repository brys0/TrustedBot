package de.pheromir.trustedbot.commands;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class KissCommand extends RandomImageCommand {

	public KissCommand() {
		this.BASE_URL = "https://nekos.life/api/v2/img/kiss";
		this.jsonKey = "url";
		this.name = "kiss";
		this.help = "Shows a random kiss gif.";
	}

}
