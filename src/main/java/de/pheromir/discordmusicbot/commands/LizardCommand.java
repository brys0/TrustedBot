package de.pheromir.discordmusicbot.commands;

import de.pheromir.discordmusicbot.commands.base.RandomImageCommand;

public class LizardCommand extends RandomImageCommand {

	public LizardCommand() {
		this.BASE_URL = "https://nekos.life/api/".intern()+"lizard".intern();
		this.jsonKey = "url".intern();
		this.name = "lizard".intern();
		this.help = "Zeigt ein zufälliges Lizard-Bild";
	}

}
