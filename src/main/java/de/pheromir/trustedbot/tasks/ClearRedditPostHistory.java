package de.pheromir.trustedbot.tasks;

import de.pheromir.trustedbot.config.GuildConfig;

public class ClearRedditPostHistory implements Runnable {

	@Override
	public void run() {
		Thread.currentThread().setName("Reddit History Clear");
		GuildConfig.clearSubredditPostHistory();
	}
 
}
