package de.pheromir.trustedbot.tasks;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.TextChannel;

public class RedditGrab implements Runnable {

	@Override
	public void run() {
		Thread.currentThread().setName("Reddit-Task");
		for (String subreddit : GuildConfig.getRedditList().keySet()) {
			if (Thread.currentThread().isInterrupted()) {
				Main.LOG.error("Reddit-Task interrupted.");
				break;
			}
			
			Unirest.get(String.format("https://www.reddit.com/r/%s/hot/.json", subreddit)).asJsonAsync(new Callback<JsonNode>() {

				@Override
				public void completed(HttpResponse<JsonNode> response) {
					if (response.getStatus() < 200 || response.getStatus() > 299) {
						Main.LOG.error("RedditGrabber received HTTP Code " + response.getStatus() + " for subreddit "
								+ subreddit);
						return;
					}
					JSONObject res = response.getBody().getObject();
					if (!res.has("data"))
						return;
					JSONObject data = res.getJSONObject("data");
					if (!data.has("children"))
						return;
					JSONArray children = data.getJSONArray("children");
					if (children.length() == 0)
						return;
					for (Object postObject : children) {
						if (Thread.currentThread().isInterrupted()) {
							Main.LOG.error("Reddit-Task interrupted.");
							break;
						}
						JSONObject post = ((JSONObject) postObject).getJSONObject("data");
						String contentUrl = post.getString("url");
						if (contentUrl.isEmpty())
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
						String thumbUrl = contentUrl.contains("v.redd.it") ? "" : "";

						for (Long chId : GuildConfig.getRedditList().get(subreddit)) {
							if (Thread.interrupted()) {
								break;
							}
							TextChannel c = Main.jda.getTextChannelById(chId);

							if (!GuildConfig.RedditPosthistoryContains(contentUrl)) {
								if (contentUrl.contains(".jpg") || contentUrl.contains(".png")
										|| contentUrl.contains(".jpeg")) {
									emb.setImage(contentUrl);
									c.sendMessage(emb.build()).queue();
								} else if (contentUrl.contains("v.redd.it")) {
									emb.setImage(thumbUrl);
									emb.setTitle("VIDEO: "
											+ (title.length() > 249 ? title.substring(0, 249) : title), link);
									c.sendMessage(emb.build()).queue();
								} else {
									c.sendMessage(emb.build()).queue();
									c.sendMessage(contentUrl).queue();
								}
							}
							c = null;
						}
						GuildConfig.addSubredditPostHistory(contentUrl);
						try {
							Thread.sleep(500L);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							Main.LOG.error("", e);
						}
					}
					res = null;
				}

				@Override
				public void failed(UnirestException e) {
					Main.LOG.error("Reddit-Task for Subreddit " + subreddit + " failed: ", e);
				}

				@Override
				public void cancelled() {
					Main.LOG.error("Reddit-Task for Subreddit " + subreddit + " was cancelled.");
				}

			});
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				Main.LOG.error("InterruptedException in Reddit-Task", e);
			}
		}
	}
}
