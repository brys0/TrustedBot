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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import de.pheromir.trustedbot.music.IcecastMeta;
import de.pheromir.trustedbot.music.YouTubeTitleCache;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class PlayingCommand extends TrustedCommand {

	String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
	Pattern compiledPattern = Pattern.compile(pattern);
	public ArrayList<YouTubeTitleCache> ytcache = new ArrayList<>();

	public PlayingCommand() {
		this.name = "playing";
		this.aliases = new String[] { "np" };
		this.help = "Shows the currently playing track.";
		this.guildOnly = true;
		this.botPermissions = new Permission[] { Permission.MESSAGE_WRITE }; 
		this.category = new Category("Music");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		AudioTrack track = gc.player.getPlayingTrack();
		if (track == null) {
			e.reply("Nothing is playing at the moment.");
			return false;
		}
		EmbedBuilder b = new EmbedBuilder();
		b.setFooter("Requested by " + (e.getGuild().getMember(gc.scheduler.getCurrentRequester()) != null
				? (e.getGuild().getMember(gc.scheduler.getCurrentRequester()).getEffectiveName())
				: gc.scheduler.getCurrentRequester().getName()), gc.scheduler.getCurrentRequester().getAvatarUrl());
		b.setColor(e.getSelfMember().getColor());
		if (!track.getInfo().uri.contains("youtube") || Main.youtubeKey.equals("none") || Main.youtubeKey.isEmpty()) {
			boolean skip = false;
			ArrayList<Field> fields = new ArrayList<>();
			if (track.getDuration() == Long.MAX_VALUE) {
				IcecastMeta icm = new IcecastMeta(track.getInfo().uri + ".xspf");
				if (!icm.getTitle().equals("Unknown (Error)")) {
					skip = true;
					fields.add(new Field("Title:", icm.getTitle(), false));
					if (icm.getCurrentListeners() != -1) {
						fields.add(new Field("Listeners:", icm.getCurrentListeners() + "", false));
					}
				}
			}
			if (!skip) {
				fields.add(new Field("Title:", track.getInfo().title, false));
			}
			fields.add(new Field("By:", track.getInfo().author, false));
			fields.add(new Field("Duration:", "[" + Methods.getTimeString(track.getPosition()) + "/"
					+ Methods.getTimeString(track.getDuration()) + "]", false));

			b.setTitle("Currently playing:");
			b.getFields().addAll(fields);
			e.reply(b.build());
			return true;
		} else {
			String videoId1 = "";
			Matcher matcher = compiledPattern.matcher(gc.player.getPlayingTrack().getInfo().uri);
			if (matcher.find()) {
				videoId1 = matcher.group();
			}
			final String videoId = videoId1;

			if (ytcache.stream().anyMatch(c -> {
				if (c.getID().equals(videoId)) {
					ArrayList<Field> fields = new ArrayList<>();
					fields.add(new Field("Channel:", c.getChannel(), false));
					fields.add(new Field("Duration:",
							"[" + Methods.getTimeString(track.getPosition()) + "/" + c.getDurationString() + "]",
							false));
					fields.add(new Field("Description:", c.getDescription(), false));

					b.setThumbnail(c.getThumbnailURL());
					b.setTitle(gc.player.isPaused() ? "Paused: "
							: "Currently playing: " + c.getTitle(), gc.player.getPlayingTrack().getInfo().uri);
					b.getFields().addAll(fields);
					e.reply(b.build());
					return true;
				}
				return false;
			})) {
				return true;
			}

			YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
					new HttpRequestInitializer() {

						@Override
						public void initialize(HttpRequest request) throws IOException {
						}
					}).setApplicationName("DiscordBot").build();

			YouTube.Videos.List videoRequest;
			try {
				videoRequest = youtube.videos().list("snippet,statistics,contentDetails");
				videoRequest.setId(videoId);
				videoRequest.setKey(Main.youtubeKey);
				VideoListResponse listResponse = videoRequest.execute();
				List<Video> videoList = listResponse.getItems();

				Video targetVideo = videoList.iterator().next();

				String title = targetVideo.getSnippet().getTitle();
				String desc = targetVideo.getSnippet().getDescription();
				desc = desc.substring(0, desc.length() > 250 ? 247 : desc.length()) + "...";
				long dur = Duration.parse(targetVideo.getContentDetails().getDuration()).toMillis();
				String author = targetVideo.getSnippet().getChannelTitle();
				String thumbnail = targetVideo.getSnippet().getThumbnails().getMedium().getUrl();
				ytcache.add(new YouTubeTitleCache(videoId1, title, dur, desc, author, thumbnail));
				ArrayList<Field> fields = new ArrayList<>();
				fields.add(new Field("Channel:", author, false));
				fields.add(new Field("Duration:", "[" + Methods.getTimeString(track.getPosition()) + "/"
						+ (dur == 0 ? "Stream" : Methods.getTimeString(dur)) + "]", false));
				fields.add(new Field("Description:", desc, false));
				b.setThumbnail(thumbnail);
				b.setTitle(gc.player.isPaused() ? "Paused: "
						: "Currently playing: " + title, gc.player.getPlayingTrack().getInfo().uri);
				b.getFields().addAll(fields);
				e.reply(b.build());
				return true;
			} catch (IOException e1) {
				Main.LOG.error("", e1);
				return false;
			}
		}
	}
}
