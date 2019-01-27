package de.pheromir.trustedbot;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.pheromir.trustedbot.tasks.CBCheck;
import de.pheromir.trustedbot.tasks.RedditGrab;
import de.pheromir.trustedbot.tasks.TwitchCheck;

public class ConsoleCommands implements Runnable {

	@Override
	public void run() {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		while(true) {
			String cmd = scan.next();
			Main.LOG.info("Command entered: "+cmd);
			
			if(cmd.equalsIgnoreCase("reddittask")) {
				if(!Main.redditTask.isCancelled()) {
					Main.redditTask.cancel(true);
				}
				Main.redditTask = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new RedditGrab(), 1, 30, TimeUnit.MINUTES);
				Main.LOG.info("Reddit-Grabber neugestartet.");
				
			} else if(cmd.equalsIgnoreCase("twitchtask")) {
				if(!Main.twitchTask.isCancelled()) {
					Main.twitchTask.cancel(true);
				}
				Main.twitchTask = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new TwitchCheck(), 1, 5, TimeUnit.MINUTES);
				Main.LOG.info("Twitch-Notifications neugestartet.");
				
			} else if(cmd.equalsIgnoreCase("cbtask")) {
				if(!Main.cbTask.isCancelled()) {
					Main.cbTask.cancel(true);
				}
				Main.cbTask = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new CBCheck(), 1, 15, TimeUnit.MINUTES);
				Main.LOG.info("CB-Task neugestartet.");
				
			} else if (cmd.equalsIgnoreCase("help")) {
				Main.LOG.info("Verfügbare Befehle: help, redditTask, twitchTask, cbTask");
				
			} else {
				Main.LOG.info("Unbekannter Befehl. 'help' für eine Liste aller Befehle.");
			}
		}
	}

}
