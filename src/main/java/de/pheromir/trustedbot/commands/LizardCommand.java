package de.pheromir.trustedbot.commands;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class LizardCommand extends RandomImageCommand {

	public LizardCommand() {
		this.BASE_URL = "https://nekos.life/api/v2/img/lizard";
		this.jsonKey = "url";
		this.name = "lizard";
		this.help = "Shows a random lizard picture.";
	}

}
