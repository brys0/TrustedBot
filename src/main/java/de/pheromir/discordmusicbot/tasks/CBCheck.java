package de.pheromir.discordmusicbot.tasks;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.Methods;
import de.pheromir.discordmusicbot.Exceptions.HttpErrorException;
import de.pheromir.discordmusicbot.config.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;

public class CBCheck implements Runnable {
	
	@Override
	public void run() {
		Thread.currentThread().setName("CB-Checker");
		for (String username : GuildConfig.getCBList().keySet()) {
			String url = "https://de.chaturbate.com/" + username;
			String res;
			try {
				res = Methods.httpRequest(url);
			} catch (HttpErrorException e1) {
				e1.printStackTrace();
				continue;
			}
			if (Main.onlineCBList.contains(username)) {
				if (res.contains("room_status: \"offline\"")) {
					Main.onlineCBList.remove(username);
				}
			} else {
				if (res.contains("room_status: \"public\"")) {
					Main.onlineCBList.add(username);

					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle(username + " ist nun online!", url);
					eb.setAuthor("Chaturbate".intern());
					eb.setThumbnail("https://ssl-ccstatic.highwebmedia.com/images/logo-standard.png".intern());
					eb.setImage("https://roomimg.stream.highwebmedia.com/ri/" + username + ".jpg");

					for (Long chId : GuildConfig.getCBList().get(username)) {
						Main.jda.getTextChannelById(chId).sendMessage(eb.build()).complete();
					}
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
