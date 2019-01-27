package de.pheromir.trustedbot.tasks;

import org.json.JSONObject;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;

public class TwitchCheck implements Runnable {

	@Override
	public void run() {
		Thread.currentThread().setName("Twitch-Task");
		for (String twitchname : GuildConfig.getTwitchList().keySet()) {
			JSONObject res = Methods.getStreamInfo(twitchname);
			if(res == null) {
				continue;
			}
			if (Main.onlineTwitchList.contains(twitchname)) {
				if (res.isNull("stream")) {
					Main.onlineTwitchList.remove(twitchname);
				}
			} else {

				if (!res.isNull("stream")) {
					Main.onlineTwitchList.add(twitchname);
					JSONObject stream = res.getJSONObject("stream");
					if (stream.getBoolean("is_playlist"))
						continue;
					String game = stream.getString("game");
					int viewers = stream.getInt("viewers");
					String preview = stream.getJSONObject("preview").getString("large");
					JSONObject channel = stream.getJSONObject("channel");
					String status = channel.getString("status");
					String displayname = channel.getString("display_name");
					String userimage = channel.getString("logo");
					String url = channel.getString("url");

					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle(status, url);
					eb.setThumbnail(userimage);
					eb.setAuthor(displayname);
					eb.setImage(preview);
					eb.addField("Game", game, true);
					eb.addField("Viewers", Integer.toString(viewers), true);

					for (Long chId : GuildConfig.getTwitchList().get(twitchname)) {
						Main.jda.getTextChannelById(chId).sendMessage("Hey @here! " + displayname + " is now live at "
								+ url + " !").embed(eb.build()).complete();
					}
				}
			}
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				Main.LOG.error("", e);
				continue;
			}
		}
	}

}
