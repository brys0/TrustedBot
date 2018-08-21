package de.pheromir.discordmusicbot.tasks;

import java.util.TimerTask;

import de.pheromir.discordmusicbot.Main;
import net.dv8tion.jda.core.entities.Guild;

public class ClearRedditPostHistory extends TimerTask {

	@Override
	public void run() {
		for(Guild g : Main.jda.getGuilds()) {
			Main.getGuildConfig(g).clearSubredditPostHistory();
		}
	}

}
