package de.pheromir.trustedbot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import de.pheromir.trustedbot.music.QueueTrack;

public class ExportCommand extends TrustedCommand {

	public ExportCommand() {
		this.name = "export";
		this.guildOnly = true;
		this.help = "Export the current Playlist to hastebin";
		this.category = new Category("Music");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		StringBuilder sb = new StringBuilder();
		if (gc.player.getPlayingTrack() != null) {
			sb.append(gc.player.getPlayingTrack().getInfo().uri + "\n");
		}
		gc.scheduler.getRequestedTitles().stream().map(QueueTrack::getTrack).map(AudioTrack::getInfo).map(track -> track.uri).forEachOrdered(track -> sb.append(track
				+ "\n"));
		String tracks = sb.toString().substring(0, sb.length() - 1);
		e.reply("Exporting playlist..");
		Unirest.post("https://hastebin.com/documents").body(tracks).asJsonAsync(new Callback<JsonNode>() {

			@Override
			public void completed(HttpResponse<JsonNode> response) {
				if (response.getStatus() != 200 || response.getBody().getObject().getString("key") == null) {
					Main.LOG.warn("Received HTTP-Code " + response.getStatus() + " while exporting Playlist");
					e.reply("Exporting playlist failed.");
				} else {
					e.reply("Exported Playlist: https://hastebin.com/" + response.getBody().getObject().getString("key")
							+ ".trustedbot");
				}
			}

			@Override
			public void failed(UnirestException ex) {
				if (Main.pastebinKey.equalsIgnoreCase("none")) {
					e.reply("Exporting playlist failed: " + ex.getLocalizedMessage());
				} else {
					e.reply("Export to Hastebin failed, trying Pastebin..");
					Unirest.post("https://pastebin.com/api/api_post.php").field("api_option", "paste").field("api_dev_key", Main.pastebinKey).field("api_paste_code", tracks).asStringAsync(new Callback<String>() {

						@Override
						public void completed(HttpResponse<String> response) {
							if (response.getStatus() != 200) {
								Main.LOG.warn("Received HTTP-Code " + response.getStatus()
										+ " while exporting Playlist to pastebin");
								e.reply("Exporting playlist failed: " + response.getStatusText());
							} else {
								e.reply("Exported Playlist: " + response.getBody());
							}
						}

						@Override
						public void failed(UnirestException ex) {
							e.reply("Exporting playlist failed: " + ex.getLocalizedMessage());
						}

						@Override
						public void cancelled() {
							e.reply("Exporting playlist cancelled.");
						}

					});
				}
			}

			@Override
			public void cancelled() {
				e.reply("Exporting playlist cancelled.");
			}

		});
		return false;
	}

}
