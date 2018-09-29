package de.pheromir.trustedbot.tasks;

import java.util.TimerTask;

import de.pheromir.trustedbot.Main;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

public class RemoveUserSuggestion extends TimerTask {

	private Guild g;
	private User user;

	public RemoveUserSuggestion(Guild g, User user) {
		this.g = g;
		this.user = user;
	}

	@Override
	public void run() {
		Main.getGuildConfig(g).getSuggestions().remove(user);
	}
 
}
