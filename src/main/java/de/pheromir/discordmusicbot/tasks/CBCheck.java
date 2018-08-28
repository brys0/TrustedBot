package de.pheromir.discordmusicbot.tasks;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.Methods;
import de.pheromir.discordmusicbot.config.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;

public class CBCheck extends TimerTask {

	@Override
	public void run() {
		HashMap<String, List<Long>> list = new HashMap<>();
		GuildConfig.getCBList().forEach((e, b) -> list.put(e, b));
		for (String username : list.keySet()) {
			String url = "https://de.chaturbate.com/" + username;
			String res;
			try {
				res = Methods.httpRequest(url);
			} catch (IOException e1) {
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
					eb.setAuthor("Chaturbate");
					eb.setThumbnail("https://ssl-ccstatic.highwebmedia.com/images/logo-standard.png");
					eb.setImage("https://roomimg.stream.highwebmedia.com/ri/" + username + ".jpg");

					for (Long chId : list.get(username)) {
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
