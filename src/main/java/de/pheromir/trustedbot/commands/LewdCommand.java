package de.pheromir.trustedbot.commands;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class LewdCommand extends RandomImageCommand {

	public LewdCommand() {
		this.name = "lewd";
		this.help = "Zeigt ein zufälliges NSFW-Neko-Bild";
		this.BASE_URL = "https://nekos.life/api/" + "lewd/" + "neko";
		this.jsonKey = "neko";
	}

}
