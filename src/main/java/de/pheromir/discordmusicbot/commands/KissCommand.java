package de.pheromir.discordmusicbot.commands;

import de.pheromir.discordmusicbot.commands.base.RandomImageCommand;

public class KissCommand extends RandomImageCommand {

	public KissCommand() {
		this.BASE_URL = "https://nekos.life/api/kiss";
		this.jsonKey = "url".intern();
		this.name = "kiss";
		this.help = "Zeigt ein zufälliges Kiss-Gif";
	}

}
