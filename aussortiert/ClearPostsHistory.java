package de.pheromir.discordmusicbot;

import java.util.TimerTask;

import de.pheromir.discordmusicbot.listener.MessageListener;

public class ClearPostsHistory extends TimerTask {

	@Override
	public void run() {
		MessageListener.nsfwRedditPosts.clear();
		if(MessageListener.serverchannel != null) {
			MessageListener.serverchannel.sendMessage("PostHistory geleert.");
		} else {
			System.out.println("PostHistory geleert. Es können nun wieder Duplikate von Posts der letzten 24 Stunden erscheinen.");
		}
	}

}
