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

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;

public class TwitchCheck implements Runnable {

	@Override
	public void run() {
		Thread.currentThread().setName("Twitch-Task");
		for (String twitchname : GuildConfig.getTwitchList().keySet()) {
			if (Thread.interrupted()) {
				break;
			}
			
			Unirest.get("https://api.twitch.tv/kraken/streams/" + twitchname
					+ "?stream_type=live").header("client-id", Main.twitchKey).asJsonAsync(new Callback<JsonNode>() {

						@Override
						public void completed(HttpResponse<JsonNode> response) {
							if (response.getStatus() < 200 || response.getStatus() > 299) {
								Main.LOG.error("Twitch-Task received HTTP Code " + response.getStatus() + " for User "
										+ twitchname);
								return;
							}
							JSONObject res = response.getBody().getObject();
							if (Main.onlineTwitchList.contains(twitchname)) {
								if (res.isNull("stream")) {
									Main.onlineTwitchList.remove(twitchname);
								}
							} else {

								if (!res.isNull("stream")) {
									Main.onlineTwitchList.add(twitchname);
									JSONObject stream = res.getJSONObject("stream");
									if (stream.getBoolean("is_playlist"))
										return;
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
										Main.jda.getTextChannelById(chId).sendMessage("Hey @here! " + displayname
												+ " is now live at " + url + " !").embed(eb.build()).complete();
									}
								}
							}
						}

						@Override
						public void failed(UnirestException e) {
							Main.LOG.error("Twitch-Task for User " + twitchname + " failed: ", e);
						}

						@Override
						public void cancelled() {
							Main.LOG.error("Twitch-Task for User " + twitchname + " cancelled.");
						}

					});
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				Main.LOG.error("InterruptionException in TwitchCheck: " + e.getMessage());
				Thread.currentThread().interrupt();
				continue;
			}
		}
	}

}
