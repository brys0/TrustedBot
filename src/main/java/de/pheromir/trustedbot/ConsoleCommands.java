/*******************************************************************************
 * Copyright (C) 2019 Pheromir
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package de.pheromir.trustedbot;

import java.time.LocalTime;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.mashape.unirest.http.Unirest;

import de.pheromir.trustedbot.tasks.RedditGrab;
import de.pheromir.trustedbot.tasks.TwitchCheck;
import net.dv8tion.jda.core.entities.Icon;

public class ConsoleCommands implements Runnable {

	@Override
	public void run() {
		Thread.currentThread().setName("CONSOLE");
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		while (true) {
			String cmd = scan.next();
			Main.LOG.info("Command entered: " + cmd);

			if (cmd.equalsIgnoreCase("reddittask")) {
				if (!Main.redditTask.isCancelled()) {
					Main.redditTask.cancel(true);
				}
				Main.redditTask = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new RedditGrab(), 1, 10, TimeUnit.MINUTES);
				Main.LOG.info("Reddit-Grabber restarted.");

			} else if (cmd.equalsIgnoreCase("twitchtask")) {
				if (!Main.twitchTask.isCancelled()) {
					Main.twitchTask.cancel(true);
				}
				Main.twitchTask = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new TwitchCheck(), 1, 5, TimeUnit.MINUTES);
				Main.LOG.info("Twitch-Notifications restarted.");

			} else if (cmd.equalsIgnoreCase("avatartask")) {
				if (!Main.avatarTask.isCancelled()) {
					Main.avatarTask.cancel(true);
				}
				Main.avatarTask = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
					try {
						Main.jda.getSelfUser().getManager().setAvatar(Icon.from(Unirest.get(Methods.getRandomAvatarURL()).asBinary().getBody())).complete();
					} catch (Exception e) {
						Main.LOG.error("", e);
					}
				}, 0, 1, TimeUnit.HOURS);
				Main.LOG.info("Avatar-Changer restarted.");

			} else if (cmd.equalsIgnoreCase("rewardtask")) {
				if (!Main.rewardTask.isCancelled()) {
					Main.rewardTask.cancel(true);
				}
				Main.rewardTask = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
					LocalTime today = LocalTime.now();
					int hour = today.getHour();
					int min = today.getMinute();
					if (hour == 0 && min == 0) {
						Main.LOG.debug("Resetting daily rewards..");
						Main.jda.getGuilds().forEach(gld -> Main.getGuildConfig(gld).resetDailyRewards());
					}
				}, 1, 1, TimeUnit.MINUTES);
				Main.LOG.info("DailyReward-Cleaner restarted.");

			} else if (cmd.equalsIgnoreCase("help")) {
				Main.LOG.info("Available commands: help, redditTask, twitchTask");
			} else if (cmd.equalsIgnoreCase("stop")) {
				Main.LOG.info("Shuttung down everything..");
				if (!Main.redditTask.isCancelled()) {
					Main.redditTask.cancel(true);
				}
				if (!Main.twitchTask.isCancelled()) {
					Main.twitchTask.cancel(true);
				}
				if (!Main.avatarTask.isCancelled()) {
					Main.avatarTask.cancel(true);
				}
				if (!Main.rewardTask.isCancelled()) {
					Main.rewardTask.cancel(true);
				}
				Main.jda.shutdownNow();
			} else {
				Main.LOG.info("Unknown command. Type 'help' for a list of commands.");
			}
		}
	}

}
