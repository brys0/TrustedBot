package de.pheromir.discordmusicbot.commands;

import de.pheromir.discordmusicbot.commands.base.RandomImageCommand;

public class LewdCommand extends RandomImageCommand {

	public LewdCommand() {
		this.name = "lewd";
		this.help = "Zeigt ein zufälliges NSFW-Neko-Bild";
		this.BASE_URL = "https://nekos.life/api/".intern()+"lewd/"+"neko".intern();
		this.jsonKey = "neko".intern();
	}

}
