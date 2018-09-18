package de.pheromir.discordmusicbot.tasks;

import de.pheromir.discordmusicbot.config.GuildConfig;

public class ClearRedditPostHistory implements Runnable {

	@Override
	public void run() {
		Thread.currentThread().setName("Reddit History Clear");
		GuildConfig.clearSubredditPostHistory();
	}
 
}
