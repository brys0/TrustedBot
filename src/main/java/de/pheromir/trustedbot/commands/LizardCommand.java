package de.pheromir.trustedbot.commands;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class LizardCommand extends RandomImageCommand {

	public LizardCommand() {
		this.BASE_URL = "https://nekos.life/api/" + "lizard";
		this.jsonKey = "url";
		this.name = "lizard";
		this.help = "Zeigt ein zufälliges Lizard-Bild";
	}

}
