package de.pheromir.discordmusicbot.tasks;

import java.util.TimerTask;

import de.pheromir.discordmusicbot.config.GuildConfig;

public class ClearRedditPostHistory extends TimerTask {

	@Override
	public void run() {
		GuildConfig.clearSubredditPostHistory();
	}

}
