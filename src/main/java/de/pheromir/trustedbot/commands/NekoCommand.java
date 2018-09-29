package de.pheromir.trustedbot.commands;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class NekoCommand extends RandomImageCommand {

	public NekoCommand() {
		this.BASE_URL = "https://nekos.life/api/" + "neko";
		this.jsonKey = "neko";
		this.name = "neko";
		this.aliases = new String[] { "catgirl" };
		this.help = "Zeigt ein zufälliges Neko-Bild";
	}

}
