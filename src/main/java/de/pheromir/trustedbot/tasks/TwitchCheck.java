/* *****************************************************************************
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

import kong.unirest.*;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.config.GuildConfig;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class TwitchCheck implements Runnable {

	@Override
	public void run() {
		Thread.currentThread().setName("Twitch-Task");
		if (Main.twitchToken.equals("none")) {
			return;
		}
		for (String twitchname : GuildConfig.getTwitchList().keySet()) {
			if (Thread.interrupted()) {
				break;
			}

			Unirest.get("https://api.twitch.tv/helix/streams/?user_login="
					+ twitchname).header("Authorization", "Bearer "
							+ Main.twitchToken).header("Client-ID", Main.twitchClientId).asJsonAsync(new Callback<JsonNode>() {

								@Override
								public void completed(HttpResponse<JsonNode> response) {
									if (response.getStatus() < 200 || response.getStatus() > 299) {
										Main.LOG.error("Twitch-Task received HTTP Code " + response.getStatus()
												+ " for User " + twitchname);
										return;
									}
									JSONObject res = response.getBody().getObject();
									JSONArray data = res.getJSONArray("data");
									if (Main.onlineTwitchList.contains(twitchname)) {
										if (data.length() == 0) {
											Main.onlineTwitchList.remove(twitchname);
										}
									} else {

										if (data.length() != 0) {
											Main.onlineTwitchList.add(twitchname);
											JSONObject stream = data.getJSONObject(0);
											if (!stream.getString("type").equals("live"))
												return;
											String gameId = stream.getString("game_id");
											int viewers = stream.getInt("viewer_count");
											String preview = stream.getString("thumbnail_url").replace("{width}", "500").replace("{height}", "300");
											String status = stream.getString("title");
											String displayname = stream.getString("user_name");

											EmbedBuilder eb = new EmbedBuilder();
											try {
												HttpResponse<JsonNode> request = Unirest.get("https://api.twitch.tv/helix/users/?login="
														+ twitchname).header("Authorization", "Bearer "
																+ Main.twitchToken).asJson();
												JSONObject userdata = request.getBody().getObject();
												if (request.getStatus() == 200
														&& userdata.getJSONArray("data").length() != 0)
													eb.setThumbnail(userdata.getJSONArray("data").getJSONObject(0).getString("profile_image_url"));
											} catch (UnirestException e) {
												Main.LOG.error("An error occurred getting userdata for " + twitchname
														+ ":", e);
												return;
											}
											try {
												HttpResponse<JsonNode> request = Unirest.get("https://api.twitch.tv/helix/games/?id="
														+ gameId).header("Authorization", "Bearer "
																+ Main.twitchToken).asJson();
												JSONObject gamedata = request.getBody().getObject();
												if (request.getStatus() == 200
														&& gamedata.getJSONArray("data").length() != 0)
													eb.addField("Game", gamedata.getJSONArray("data").getJSONObject(0).getString("name"), true);
											} catch (UnirestException e) {
												Main.LOG.error("An error occurred getting userdata for " + twitchname
														+ ":", e);
												return;
											}
											eb.setTitle(status, "https://twitch.tv/" + twitchname);
											eb.setAuthor(displayname);
											eb.setImage(preview);
											eb.addField("Viewers", Integer.toString(viewers), true);

											for (Long chId : GuildConfig.getTwitchList().get(twitchname)) {
												TextChannel tc = Main.jda.getTextChannelById(chId);
												if (tc == null) {
													continue;
												}
												tc.sendMessage("Hey @here! "
														+ displayname + " is now live at https://twitch.tv/"
														+ twitchname + " !").embed(eb.build()).complete();
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
			}
		}
	}

}
