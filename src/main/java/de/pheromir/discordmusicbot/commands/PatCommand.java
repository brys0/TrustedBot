package de.pheromir.discordmusicbot.commands;

import de.pheromir.discordmusicbot.commands.base.RandomImageCommand;

public class PatCommand extends RandomImageCommand {

	public PatCommand() {
		this.BASE_URL = "https://nekos.life/api/".intern()+"pat".intern();
		this.jsonKey = "url".intern();
		this.name = "pat".intern();
		this.help = "Zeigt ein zufälliges Pat-Gif";
	}

}
