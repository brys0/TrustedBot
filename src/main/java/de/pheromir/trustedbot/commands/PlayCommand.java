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
package de.pheromir.trustedbot.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import de.pheromir.trustedbot.music.Suggestion;
import de.pheromir.trustedbot.tasks.RemoveUserSuggestion;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class PlayCommand extends TrustedCommand {

	String pattern = "^^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	Pattern compiledPattern = Pattern.compile(pattern);

	public PlayCommand() {
		this.name = "play";
		this.botPermissions = new Permission[] { Permission.VOICE_CONNECT, Permission.VOICE_SPEAK };
		this.guildOnly = true;
		this.arguments = "<Keywords/URL> [--playlist]";
		this.help = "Add a track to the playlist.";
		this.category = new Category("Music");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		User user = e.getAuthor();

		// No arguments
		if (args.length == 0) {
			e.reply(usage);
			return false;
		}

		// Load a suggestion
		if (gc.getSuggestions().containsKey(user) && args.length == 1
				&& args[0].matches(String.format("^([1-%s]{1})$", gc.getSuggestions().get(user).size()))) {
			try {
				int selSuggest = Integer.parseInt(args[0]);

				PlayCommand.loadTrack(e, String.format("https://youtube.com/watch?v=%s", gc.getSuggestions().get(user).get(selSuggest
						- 1).getId()), false);
				return true;
			} catch (NumberFormatException ex) {
				e.reply("An error occurred while parsing your input.");
				return false;
			}
		}

		// Check if argument is spotify url
		if (!Main.spotifyToken.equals("none")) {
			if (e.getArgs().toLowerCase().contains("spotify.com/track/")) {
				Pattern p = Pattern.compile("track\\/(?<trackid>[0-9A-z]+)");
				Matcher m = p.matcher(e.getArgs());
				if (m.find()) {
					String id = m.group("trackid");
					if (id != null && !id.isEmpty()) {
						Unirest.get("https://api.spotify.com/v1/tracks/{id}").routeParam("id", id).header("Authorization", "Bearer "
								+ Main.spotifyToken).asJsonAsync(new Callback<JsonNode>() {

									@Override
									public void cancelled() {
										e.reply("Getting track information from spotify has been cancelled.");
									}

									@Override
									public void completed(HttpResponse<JsonNode> arg0) {
										String title = arg0.getBody().getObject().getString("name");
										String artist = ((JSONObject) arg0.getBody().getObject().getJSONArray("artists").get(0)).getString("name");

										try {
											printAndAddSuggestions(getVideoSuggestions(artist + " " + title), e);
										} catch (IOException e1) {
											Main.LOG.error("", e1);
											e.reply("An error occurred while getting track information from spotify.");
										}
										return;
									}

									@Override
									public void failed(UnirestException ex) {
										Main.LOG.error("Spotify Track fetch failed: ", ex);
										e.reply("An error occurred while getting track information from spotify.");
									}

								});
						return true;
					}
				}
			} else if (e.getArgs().toLowerCase().contains("spotify.com")) {
				e.reply("Currently only tracks (not playlists) from spotify are supported.\n(spotify.com/track/TRACKID)");
				return false;
			}

		}
		
		
		Matcher urlMatcher = compiledPattern.matcher(e.getArgs());
		// Arguments not matching an URL -> Search on youtube
		if (!urlMatcher.find()) {
			try {
				printAndAddSuggestions(getVideoSuggestions(e.getArgs()), e);
				return true;
			} catch (IOException ex) {
				Main.LOG.error("Error printing music suggestions: ", ex);
				e.reply("An error occurred while getting suggestions.");
				return false;
			}
		}
		
		// Load track(s) from given URL
		loadTrack(e, e.getArgs().replaceAll("--playlist", ""), e.getArgs().contains("--playlist"));
		return true;
	}

	public static void loadTrack(CommandEvent e, String toLoad, boolean loadPlaylist) {
		VoiceChannel vc = e.getMember().getVoiceState().getChannel();
		AudioManager audioManager = e.getGuild().getAudioManager();
		GuildConfig musicManager = Main.getGuildConfig(e.getGuild());
		Main.playerManager.loadItemOrdered(musicManager, toLoad, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack track) {
				if (track.getDuration() > (1000 * 60 * 60 * 2)
						&& !Main.getExtraUsers().contains(e.getAuthor().getIdLong())) {
					e.reply("You can only add tracks with a duration of up to two hours.");
					return;
				} else if (Main.getGuildConfig(e.getGuild()).scheduler.getRequestedTitles().size() > 10
						&& !Main.getExtraUsers().contains(e.getAuthor().getIdLong())) {
					e.reply("You can't add more tracks, as the limit of 10 tracks in the queue has been reached.");
					return;
				}
				audioManager.openAudioConnection(vc);
				musicManager.scheduler.queue(track, e.getAuthor());
				e.reply("`" + track.getInfo().title + "` [" + Methods.getTimeString(track.getDuration())
						+ "] has been added to the queue.");
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				AudioTrack firstTrack = playlist.getSelectedTrack();
				if (firstTrack == null) {
					firstTrack = playlist.getTracks().get(0);
				}
				audioManager.openAudioConnection(vc);
				if (loadPlaylist) {
					StringBuilder sb = new StringBuilder();
					playlist.getTracks().forEach(track -> {
						musicManager.scheduler.queue(track, e.getAuthor());
						sb.append("`" + track.getInfo().title + "` [" + Methods.getTimeString(track.getDuration())
								+ "] has been added to the queue.\n");
					});
					e.reply(sb.toString());
				} else {
					musicManager.scheduler.queue(firstTrack, e.getAuthor());
					e.reply("`" + firstTrack.getInfo().title + "` [" + Methods.getTimeString(firstTrack.getDuration())
							+ "] has been added to the queue.");
				}

			}

			@Override
			public void noMatches() {
				e.reply("Couldn't find a matching track.");
			}

			@Override
			public void loadFailed(FriendlyException throwable) {
				e.reply("An error occurred loading the track: " + throwable.getLocalizedMessage());
			}

		});
	}

	public static List<SearchResult> getVideoSuggestions(String search) throws IOException {
		YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
				new HttpRequestInitializer() {

					public void initialize(HttpRequest request) throws IOException {
					}
				}).setApplicationName("TrustedBot").build();

		YouTube.Search.List searchRequest;
		searchRequest = youtube.search().list("snippet");
		searchRequest.setMaxResults((long) 5);
		searchRequest.setType("video");
		searchRequest.setQ(search);
		searchRequest.setKey(Main.youtubeKey);
		SearchListResponse listResponse = searchRequest.execute();
		List<SearchResult> videoList = listResponse.getItems();
		return videoList;
	}

	public static void printAndAddSuggestions(List<SearchResult> videoList, CommandEvent e) {
		MessageBuilder mes = new MessageBuilder();
		mes.append("**Titelauswahl:**");
		EmbedBuilder m = new EmbedBuilder();
		m.setTitle("Suggestions for " + e.getMember().getEffectiveName() + ":");
		m.setColor(e.getGuild().getSelfMember().getColor());
		ArrayList<Suggestion> suggests = new ArrayList<>();
		for (int i = 0; i < (videoList.size() >= 5 ? 5 : videoList.size()); i++) {
			suggests.add(new Suggestion(videoList.get(i).getSnippet().getTitle(),
					videoList.get(i).getId().getVideoId()));
			m.appendDescription("**[" + (i + 1) + "]** " + videoList.get(i).getSnippet().getTitle() + " *["
					+ Methods.getTimeString(Methods.getYoutubeDuration(videoList.get(i).getId().getVideoId()))
					+ "]*\n\n");
		}
		if (suggests.size() > 0) {
			Main.getGuildConfig(e.getGuild()).getSuggestions().put(e.getAuthor(), suggests);
			m.setFooter("Select track: !play [Nr] (Suggestions are valid for 5 min)", e.getJDA().getSelfUser().getAvatarUrl());
		} else {
			Main.getGuildConfig(e.getGuild()).getSuggestions().remove(e.getAuthor());
			m.appendDescription("No matching videos found");
		}
		Executors.newScheduledThreadPool(1).schedule(new RemoveUserSuggestion(e.getGuild(),
				e.getAuthor()), 5, TimeUnit.MINUTES);
		mes.setEmbed(m.build());
		e.reply(mes.build());
	}

}
