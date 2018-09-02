package de.pheromir.discordmusicbot.commands;

import de.pheromir.discordmusicbot.commands.base.RandomImageCommand;

public class HugCommand extends RandomImageCommand {

	public HugCommand() {
		this.BASE_URL = "https://nekos.life/api/hug";
		this.jsonKey = "url".intern();
		this.name = "hug";
		this.help = "Zeigt ein zufälliges Hug-Gif";
	}

}
