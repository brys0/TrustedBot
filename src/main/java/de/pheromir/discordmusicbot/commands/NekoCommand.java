package de.pheromir.discordmusicbot.commands;

import de.pheromir.discordmusicbot.commands.base.RandomImageCommand;

public class NekoCommand extends RandomImageCommand {

	public NekoCommand() {
		this.BASE_URL = "https://nekos.life/api/".intern() + "neko".intern();
		this.jsonKey = "neko".intern();
		this.name = "neko".intern();
		this.aliases = new String[] { "catgirl" };
		this.help = "Zeigt ein zufälliges Neko-Bild";
	}

}
