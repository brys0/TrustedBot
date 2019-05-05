package de.pheromir.trustedbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
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
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class ImportCommand extends TrustedCommand {

	public final String PATTERN = "^https?:\\/\\/(paste|haste)bin.com\\/([\\w\\d]+)(\\.[\\w\\d]*)?$";

	public ImportCommand() {
		this.name = "import";
		this.guildOnly = true;
		this.arguments = "<pastebin/hastebin link>";
		this.botPermissions = new Permission[] { Permission.VOICE_CONNECT, Permission.VOICE_SPEAK };
		this.help = "Import a playlist from haste-/pastebin";
		this.category = new Category("Music");
	}

	@Override
	protected boolean exec(CommandEvent e) {
		GuildConfig musicManager = Main.getGuildConfig(e.getGuild());
		AudioManager audioManager = e.getGuild().getAudioManager();
		VoiceChannel vc = e.getMember().getVoiceState().getChannel();

		if (args.length != 1) {
			e.reply("Syntaxerror. Usage: " + this.name + " " + this.arguments);
			return false;
		}
		Pattern p = Pattern.compile(PATTERN);
		Matcher m = p.matcher(args[0]);
		if (m.find()) {
			String service = m.group(1);
			String pasteid = m.group(2);
			String getUrl = "https://" + service + "bin.com/raw/" + pasteid;
			e.reply("Trying to import playlist..");
			Unirest.get(getUrl).asStringAsync(new Callback<String>() {

				@Override
				public void completed(HttpResponse<String> response) {
					if (response.getStatus() != 200) {
						Main.LOG.warn("Received HTTP-Code " + response.getStatus() + " while exporting Playlist");
						e.reply("Importing playlist failed.");
					} else {
//						System.out.println("\""+response.getBody()+"\"");
						for (String trackUrl : response.getBody().split("\\r?\\n")) {
//							System.out.println(trackUrl);
							Main.playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

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
									e.reply("`" + track.getInfo().title + "` ["
											+ Methods.getTimeString(track.getDuration())
											+ "] has been added to the queue.");
								}

								@Override
								public void playlistLoaded(AudioPlaylist playlist) {
									AudioTrack firstTrack = playlist.getSelectedTrack();
									if (firstTrack == null) {
										firstTrack = playlist.getTracks().get(0);
									}
									audioManager.openAudioConnection(vc);
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
					}
				}

				@Override
				public void failed(UnirestException ex) {
					e.reply("Failed to read playlist from URL.");
				}

				@Override
				public void cancelled() {
					e.reply("Reading playlist from URL cancelled.");
				}

			});
		} else {
			e.reply("Could not parse URL. Please use an URL in the following format: `https://hastebin.com/pasteid` or `https://pastebin.com/pasteid`");
			return false;
		}

		return false;
	}

}
