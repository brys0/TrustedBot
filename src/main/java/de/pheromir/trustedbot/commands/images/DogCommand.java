package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class DogCommand extends RandomImageCommand {

	public DogCommand() {
		this.name = "dog";
		this.help = "Shows a random dog picture.";
		this.BASE_URL = "https://nekos.life/api/v2/img/woof";
		this.jsonKey = "url";
	}
}
