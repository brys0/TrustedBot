package de.pheromir.discordmusicbot.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import org.json.JSONObject;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.Methods;
import de.pheromir.discordmusicbot.config.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;

public class TwitchCheck extends TimerTask {

	@Override
	public void run() {
		HashMap<String, List<Long>> list = new HashMap<>();
		GuildConfig.getTwitchList().forEach((e, b) -> list.put(e, b));
		for (String twitchname : list.keySet()) {
			JSONObject res = Methods.getStreamInfo(twitchname);
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
					eb.addField("Spiel", game, true);
					eb.addField("Zuschauer", Integer.toString(viewers), true);

					for (Long chId : list.get(twitchname)) {
						Main.jda.getTextChannelById(chId).sendMessage("Hey @here! " + displayname + " ist nun auf "
								+ url + " online!").embed(eb.build()).complete();
					}
				}
			}
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
