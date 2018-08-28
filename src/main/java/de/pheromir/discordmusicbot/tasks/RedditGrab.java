package de.pheromir.discordmusicbot.tasks;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.Methods;
import de.pheromir.discordmusicbot.config.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.TextChannel;

public class RedditGrab extends TimerTask {

	@Override
	public void run() {
		HashMap<String, List<Long>> list = new HashMap<>();
		GuildConfig.getRedditList().forEach((e, b) -> list.put(e, b));
		for (String subreddit : list.keySet()) {
			try {
				JSONObject res = Methods.httpRequestJSON("https://www.reddit.com/r/" + subreddit + "/hot/.json");
				if (!res.has("data"))
					continue;
				JSONObject data = res.getJSONObject("data");
				if (!data.has("children"))
					continue;
				JSONArray children = data.getJSONArray("children");
				if (children.length() == 0)
					continue;
				for (Object postObject : children) {
					JSONObject post = ((JSONObject) postObject).getJSONObject("data");
					String contentUrl = post.getString("url");
					if (contentUrl.isEmpty() || contentUrl.equals(""))
						continue;

					String sub = post.getString("subreddit");
					String title = post.getString("title");
					String link = "https://www.reddit.com" + post.getString("permalink");
					String author = post.getString("author");
					int score = post.getInt("score");
					
					EmbedBuilder emb = new EmbedBuilder();
					emb.setAuthor(author);
					emb.addField(new Field("Score", score + "", false));
					emb.setFooter("/r/" + sub, Main.jda.getSelfUser().getAvatarUrl());
					emb.setTitle(title.length() > 256 ? title.substring(0, 256) : title, link);

					for (Long chId : list.get(subreddit)) {
						TextChannel c = Main.jda.getTextChannelById(chId);
						
						if (!GuildConfig.RedditPosthistoryContains(contentUrl)) {
							if (contentUrl.contains(".jpg") || contentUrl.contains(".png")
									|| contentUrl.contains(".jpeg")) {
								emb.setImage(contentUrl);
								c.sendMessage(emb.build()).complete();
							} else {
								c.sendMessage(emb.build()).complete();
								c.sendMessage(contentUrl).complete();
							}
						}
					}
					GuildConfig.addSubredditPostHistory(contentUrl);
					Thread.sleep(500L);
				}
			} catch (IOException e) {
				System.out.print("Fehler bei RedditGrab: ");
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
