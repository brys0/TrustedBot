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
package de.pheromir.trustedbot.tasks;

import com.google.gson.internal.bind.util.ISO8601Utils;
import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.config.GuildConfig;
import de.pheromir.trustedbot.misc.RedditSubscription.SortType;
import kong.unirest.*;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Instant;


public class RedditGrab implements Runnable {

    @Override
    public void run() {
        Thread.currentThread().setName("Reddit-Task");
        try {
            for (String subreddit : GuildConfig.getRedditList().keySet()) {
                Main.LOG.debug("Checking Subreddit " + subreddit);
                if (Thread.currentThread().isInterrupted()) {
                    Main.LOG.error("Reddit-Task interrupted.");
                    break;
                }

                for (SortType sortType : SortType.values()) {
                    if (Thread.currentThread().isInterrupted()) {
                        Main.LOG.error("Reddit-Task interrupted.");
                        break;
                    }
                    if (GuildConfig.getRedditList().get(subreddit).containsSorting(sortType)) {
                        Unirest.get(String.format("https://www.reddit.com/r/%s/%s/.json", subreddit, sortType.name().toLowerCase())).asJsonAsync(new Callback<JsonNode>() {

                            @Override
                            public void completed(HttpResponse<JsonNode> response) {
                                if (response.getStatus() < 200 || response.getStatus() > 299) {
                                    Main.LOG.error("RedditGrabber received HTTP Code " + response.getStatus()
                                            + " for subreddit " + subreddit + " (" + sortType.name() + ")");
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
                                    String sub = post.getString("subreddit");
                                    String title = post.getString("title");
                                    String link = "https://www.reddit.com" + post.getString("permalink");
                                    String author = post.getString("author");
                                    String postText = post.getString("selftext");
                                    if (postText.length() > 2000) {
                                        postText = postText.substring(0, 2000) + "..\n[Open post to read the full text]";
                                    }

                                    int score = post.getInt("score");

                                    EmbedBuilder emb = new EmbedBuilder();
                                    emb.setTimestamp(Instant.ofEpochMilli(post.getLong("created_utc") * 1000));
                                    emb.setAuthor(author);
                                    emb.setDescription(postText);
                                    emb.addField(new Field("Score", score + "", false));
                                    emb.setFooter("/r/" + sub + " (" + sortType.name().toLowerCase()
                                            + ")", Main.jda.getSelfUser().getAvatarUrl());
                                    emb.setTitle(title.length() > 256 ? title.substring(0, 256) : title, link);
                                    String thumbUrl = contentUrl.contains("v.redd.it") ? post.getString("thumbnail") : "";
                                    for (Long chId : GuildConfig.getRedditList().get(subreddit).getChannels(sortType)) {
                                        if (Thread.interrupted()) {
                                            break;
                                        }
                                        TextChannel c = Main.jda.getTextChannelById(chId);
                                        if (c == null) {
                                            continue;
                                        }

                                        if (!GuildConfig.RedditPosthistoryContains(contentUrl)) {
                                            if (contentUrl.contains(".jpg") || contentUrl.contains(".png")
                                                    || contentUrl.contains(".jpeg") || contentUrl.contains(".gif")) {
                                                emb.setImage(contentUrl);
                                                c.sendMessage(emb.build()).queue();
                                            } else if (contentUrl.contains("v.redd.it")) {
                                                if (thumbUrl.contains(".")) {
                                                    emb.setImage(thumbUrl);
                                                }
                                                emb.setTitle("VIDEO: "
                                                        + (title.length() > 249 ? title.substring(0, 249) : title), link);
                                                c.sendMessage(emb.build()).queue();
                                            } else if (contentUrl.contains("/comments/")) {
                                                c.sendMessage(emb.build()).queue();
                                            } else {
                                                c.sendMessage(emb.build()).queue();
                                                c.sendMessage(contentUrl).queue();
                                            }
                                        }
                                    }
                                    GuildConfig.addSubredditPostHistory(contentUrl);
//                                    try {
//                                        Thread.sleep(50L);
//                                    } catch (InterruptedException e) {
//                                        Thread.currentThread().interrupt();
//                                        Main.LOG.error("", e);
//                                    }
                                }
                            }

                            @Override
                            public void failed(UnirestException e) {
                                Main.LOG.error("Reddit-Task for Subreddit " + subreddit + " (" + sortType.name()
                                        + ") failed: ", e);
                            }

                            @Override
                            public void cancelled() {
                                Main.LOG.error("Reddit-Task for Subreddit " + subreddit + " (" + sortType.name()
                                        + ") was cancelled.");
                            }

                        });
//                        try {
//                            Thread.sleep(100L);
//                        } catch (InterruptedException e) {
//                            Thread.currentThread().interrupt();
//                            Main.LOG.error("InterruptedException in Reddit-Task", e);
//                        }
                    }
//                    try {
//                        Thread.sleep(100L);
//                    } catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                        Main.LOG.error("InterruptedException in Reddit-Task", e);
//                    }
                }

            }
        } catch (Exception e) {
            Main.LOG.error("Exception in Reddit-Task: ", e); // You really shouldn't do that
        }
    }
}
