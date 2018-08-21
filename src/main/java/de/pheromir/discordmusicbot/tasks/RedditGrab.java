package de.pheromir.discordmusicbot.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.Methods;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class RedditGrab extends TimerTask {

	@Override
	public void run() {
		ArrayList<String> list = new ArrayList<>();
		list.addAll(Main.generalRedditList);
		for (String subreddit : list) {
			try {
				JSONObject res = Methods.httpRequest("https://www.reddit.com/r/" + subreddit + "/hot/.json");
				if (!res.has("data"))
					continue;
				JSONObject data = res.getJSONObject("data");
				if (!data.has("children"))
					continue;
				JSONArray children = data.getJSONArray("children");
				if (children.length() == 0)
					continue;
				for (Object postObject : children) {
					for (Guild g : Main.jda.getGuilds()) {
						JSONObject post = ((JSONObject) postObject).getJSONObject("data");
						if (Main.getGuildConfig(g).getRedditList().containsKey(subreddit)) {
							for (Long chId : Main.getGuildConfig(g).getRedditList().get(subreddit)) {
								String sub = post.getString("subreddit");
								String title = post.getString("title");
								String link = "https://www.reddit.com"+post.getString("permalink");
								String contentUrl = post.getString("url");
								String author = post.getString("author");
								int score = post.getInt("score");
								EmbedBuilder emb = new EmbedBuilder();
								emb.setAuthor(author);
								emb.addField(new Field("Score", score+"", false));
								emb.setFooter("/r/"+sub, Main.jda.getSelfUser().getAvatarUrl());
								emb.setTitle(title, link);
								emb.setColor(g.getSelfMember().getColor());
								if (!Main.getGuildConfig(g).getSubredditPostHistory(subreddit).contains(contentUrl)) {
									if (!contentUrl.contains("gfy") && !contentUrl.contains(".gif")) {
										emb.setImage(contentUrl);
										Main.jda.getTextChannelById(chId).sendMessage(emb.build()).complete();
									} else {
										Main.jda.getTextChannelById(chId).sendMessage(emb.build()).complete();
										Main.jda.getTextChannelById(chId).sendMessage(contentUrl).complete();
									}
								}
								Main.getGuildConfig(g).addSubredditPostHistory(subreddit, contentUrl);
							}
							Main.getGuildConfig(g).addSubredditPostHistory(subreddit, post.getString("url"));
							Thread.sleep(1000L);
						}
					}
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
