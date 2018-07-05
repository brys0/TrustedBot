package de.pheromir.discordmusicbot.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.Methods;
import de.pheromir.discordmusicbot.handler.GuildMusicManager;
import de.pheromir.discordmusicbot.helper.Suggestion;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class PlayCommand extends Command {

	String pattern = "^^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	Pattern compiledPattern = Pattern.compile(pattern);

	public PlayCommand() {
		this.name = "play";
		this.botPermissions = new Permission[] { Permission.VOICE_CONNECT, Permission.VOICE_SPEAK };
		this.guildOnly = true;
		this.help = "Titel zur Wiedergabeschlange hinzufügen.";
	}

	@Override
	protected void execute(CommandEvent e) {
		if (e.getAuthor().isBot())
			return;
		VoiceChannel vc = e.getMember().getVoiceState().getChannel();
		if (vc == null) {
			e.reply("Du musst dich in einem VoiceChannel befinden.");
		}
		if (!e.getSelfMember().hasPermission(vc, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)) {
			e.reply("Ich habe keine Rechte in deinem Channel Musik abzuspielen.");
		}

		AudioManager audioManager = e.getGuild().getAudioManager();
		GuildMusicManager musicManager = Main.getGuildAudioPlayer(e.getGuild());

		if (e.getArgs().isEmpty() && musicManager.player.isPaused()) {
			musicManager.player.setPaused(false);
			e.reply("Wiedergabe fortgesetzt.");
			if (Main.getGuildAudioPlayer(e.getGuild()).player.getPlayingTrack() != null && !e.getGuild().getAudioManager().isConnected()) {
				e.getGuild().getAudioManager().openAudioConnection(vc);
			}
			return;
		} else if (e.getArgs().isEmpty()) {
			e.reply("Bitte einen Track angeben (z.B. Link / Youtube Suchbegriffe).");
			return;
		}

		String toLoad = "";

		if (musicManager.suggestions.containsKey(e.getAuthor()) && (e.getArgs().equalsIgnoreCase("1") || e.getArgs().equalsIgnoreCase("2") || e.getArgs().equalsIgnoreCase("3")
				|| e.getArgs().equalsIgnoreCase("4") || e.getArgs().equalsIgnoreCase("5"))) {
			int nr = Integer.parseInt(e.getArgs());
			if (nr <= musicManager.suggestions.get(e.getAuthor()).size()) {
				toLoad = "http://youtube.com/watch?v=" + musicManager.suggestions.get(e.getAuthor()).get(nr - 1).getId();
			}
		} else if (e.getArgs().equalsIgnoreCase("iloveradio")) {
			toLoad = "http://www.iloveradio.de/iloveradio.m3u";
		}
		
		if (toLoad.equals("")) {
			Matcher matcher = compiledPattern.matcher(e.getArgs());
			if (!matcher.find()) {
				YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {

					public void initialize(HttpRequest request) throws IOException {
					}
				}).setApplicationName("DiscordBot").build();

				YouTube.Search.List searchRequest;
				try {

					searchRequest = youtube.search().list("snippet");
					searchRequest.setMaxResults((long) 5);
					searchRequest.setType("video");
					searchRequest.setQ(e.getArgs());
					searchRequest.setKey(Main.youtubeKey);
					SearchListResponse listResponse = searchRequest.execute();
					List<SearchResult> videoList = listResponse.getItems();
					MessageBuilder mes = new MessageBuilder();
					mes.append("**Titelauswahl:**");
					EmbedBuilder m = new EmbedBuilder();
					m.setColor(e.getGuild().getSelfMember().getColor());
					ArrayList<Suggestion> suggests = new ArrayList<>();
					for (int i = 0; i < (videoList.size() >= 5 ? 5 : videoList.size()); i++) {
						suggests.add(new Suggestion(videoList.get(i).getSnippet().getTitle(), videoList.get(i).getId().getVideoId()));
						m.appendDescription("**[" + (i + 1) + "]** " + videoList.get(i).getSnippet().getTitle() + " *["
								+ Methods.getTimeString(Methods.getYoutubeDuration(videoList.get(i).getId().getVideoId())) + "]*\n\n");
					}
					musicManager.suggestions.put(e.getAuthor(), suggests);
					m.setFooter("Titel auswählen: !play [Nr]", e.getJDA().getSelfUser().getAvatarUrl());
					mes.setEmbed(m.build());
					e.reply(mes.build());

					return;

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				toLoad = matcher.group();
			}
		}
		musicManager.setAutoPause(false);
		musicManager.player.setPaused(false);
		Main.playerManager.loadItemOrdered(musicManager, toLoad, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack track) {
				audioManager.openAudioConnection(vc);
				musicManager.scheduler.queue(track, e.getAuthor());
				e.reply("`" + track.getInfo().title + "` [" + Methods.getTimeString(track.getDuration()) + "] wurde zur Warteschlange hinzugefügt.");
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				AudioTrack firstTrack = playlist.getSelectedTrack();
				if (firstTrack == null) {
					firstTrack = playlist.getTracks().get(0);
				}
				audioManager.openAudioConnection(vc);
				musicManager.scheduler.queue(firstTrack, e.getAuthor());
				e.reply("`" + firstTrack.getInfo().title + "` [" + Methods.getTimeString(firstTrack.getDuration()) + "] wurde zur Warteschlange hinzugefügt.");
			}

			@Override
			public void noMatches() {
				e.reply("Es konnte kein passender Titel gefunden werden.");
			}

			@Override
			public void loadFailed(FriendlyException throwable) {
				e.reply("Fehler beim Abspielen: " + throwable.getLocalizedMessage());
			}

		});

	}

}
