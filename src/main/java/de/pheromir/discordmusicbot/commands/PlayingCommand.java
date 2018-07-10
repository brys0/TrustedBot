package de.pheromir.discordmusicbot.commands;

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
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.Methods;
import de.pheromir.discordmusicbot.helper.GuildConfig;
import de.pheromir.discordmusicbot.helper.IcecastMeta;
import de.pheromir.discordmusicbot.helper.YouTubeTitleCache;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class PlayingCommand extends Command {

	String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
	Pattern compiledPattern = Pattern.compile(pattern);
	public ArrayList<YouTubeTitleCache> ytcache = new ArrayList<>();

	public PlayingCommand() {
		this.name = "playing";
		this.aliases = new String[] { "np" };
		this.help = "";
		this.guildOnly = true;
		this.botPermissions = new Permission[] { Permission.MESSAGE_WRITE };
		this.category = new Category("Music");
	}

	@Override
	protected void execute(CommandEvent e) {
		if (e.getAuthor().isBot())
			return;

		GuildConfig m = Main.getGuildConfig(e.getGuild());
		AudioTrack track = m.player.getPlayingTrack();
		if (track == null) {
			e.reply("Derzeit wird nichts gespielt.");
			return;
		}
		if (!track.getInfo().uri.contains("youtube") || Main.youtubeKey.equals("none") || Main.youtubeKey.isEmpty()) {
			boolean skip = false;
			ArrayList<Field> fields = new ArrayList<>();
			if (track.getDuration() == Long.MAX_VALUE) {
				IcecastMeta icm = new IcecastMeta(track.getInfo().uri + ".xspf");
				if (!icm.getTitle().equals("Unbekannt (Fehler")) {
					skip = true;
					fields.add(new Field("Titel:", icm.getTitle(), false));
					if (icm.getCurrentListeners() != -1) {
						fields.add(new Field("Zuhörer:", icm.getCurrentListeners() + "", false));
					}
				}
			}
			if (!skip) {
				fields.add(new Field("Titel:", track.getInfo().title, false));
			}
			fields.add(new Field("Von:", track.getInfo().author, false));
			fields.add(new Field("Zeit:", "[" + Methods.getTimeString(track.getPosition()) + "/"
					+ Methods.getTimeString(track.getDuration()) + "]", false));
			EmbedBuilder b = new EmbedBuilder();
			b.setTitle(m.player.isPaused() ? "Pausiert:" : "Derzeit läuft:");
			b.setColor(e.getSelfMember().getColor());
			b.getFields().addAll(fields);
			b.setFooter("Hinzugefügt von " + (e.getGuild().getMember(m.scheduler.getCurrentRequester()) != null
					? (e.getGuild().getMember(m.scheduler.getCurrentRequester()).getNickname() != null
							? e.getGuild().getMember(m.scheduler.getCurrentRequester()).getNickname()
							: m.scheduler.getCurrentRequester().getName())
					: m.scheduler.getCurrentRequester().getName()), m.scheduler.getCurrentRequester().getAvatarUrl());
			e.reply(b.build());
			return;
		} else {

			String videoId1 = "Hl-zzrqQoSE";
			Matcher matcher = compiledPattern.matcher(m.player.getPlayingTrack().getInfo().uri);
			if (matcher.find()) {
				videoId1 = matcher.group();
			}
			final String videoId = videoId1;

			if (ytcache.stream().anyMatch(c -> {
				if (c.getID().equals(videoId)) {
					ArrayList<Field> fields = new ArrayList<>();
					fields.add(new Field("Kanal:", c.getChannel(), false));
					fields.add(new Field("Dauer:",
							"[" + Methods.getTimeString(track.getPosition()) + "/" + c.getDurationString() + "]",
							false));
					fields.add(new Field("Beschreibung:", c.getDescription(), false));

					EmbedBuilder b = new EmbedBuilder();
					b.setThumbnail(c.getThumbnailURL());
					b.setTitle(m.player.isPaused() ? "Pausiert: "
							: "Derzeit läuft: " + c.getTitle(), m.player.getPlayingTrack().getInfo().uri);
					b.setColor(e.getSelfMember().getColor());
					b.getFields().addAll(fields);
					b.setFooter("Hinzugefügt von " + (e.getGuild().getMember(m.scheduler.getCurrentRequester()) != null
							? (e.getGuild().getMember(m.scheduler.getCurrentRequester()).getNickname() != null
									? e.getGuild().getMember(m.scheduler.getCurrentRequester()).getNickname()
									: m.scheduler.getCurrentRequester().getName())
							: m.scheduler.getCurrentRequester().getName()), m.scheduler.getCurrentRequester().getAvatarUrl());
					e.reply(b.build());
					return true;
				}
				return false;
			}))
				return;

			YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
					new HttpRequestInitializer() {

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
				fields.add(new Field("Kanal:", author, false));
				fields.add(new Field("Dauer:", "[" + Methods.getTimeString(track.getPosition()) + "/"
						+ (dur == 0 ? "Stream" : Methods.getTimeString(dur)) + "]", false));
				fields.add(new Field("Beschreibung:", desc, false));

				EmbedBuilder b = new EmbedBuilder();
				b.setThumbnail(thumbnail);
				b.setTitle(m.player.isPaused() ? "Pausiert: "
						: "Derzeit läuft: " + title, m.player.getPlayingTrack().getInfo().uri);
				b.setColor(e.getSelfMember().getColor());
				b.getFields().addAll(fields);
				b.setFooter("Hinzugefügt von "
						+ e.getGuild().getMember(m.scheduler.getCurrentRequester()).getEffectiveName(), m.scheduler.getCurrentRequester().getAvatarUrl());
				e.reply(b.build());

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
