package de.pheromir.trustedbot.commands;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class KissCommand extends RandomImageCommand {

	public KissCommand() {
		this.BASE_URL = "https://nekos.life/api/kiss";
		this.jsonKey = "url";
		this.name = "kiss";
		this.help = "Zeigt ein zufälliges Kiss-Gif";
	}

}
