package de.pheromir.trustedbot.commands.images;

import de.pheromir.trustedbot.commands.base.RandomImageCommand;

public class PokeCommand extends RandomImageCommand {

	public PokeCommand() {
		this.BASE_URL = "https://nekos.life/api/v2/img/poke";
		this.jsonKey = "url";
		this.name = "poke";
		this.help = "Shows a random poke gif.";
	}

}
