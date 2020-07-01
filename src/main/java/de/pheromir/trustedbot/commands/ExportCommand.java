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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import de.pheromir.trustedbot.music.QueueTrack;
import kong.unirest.*;

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
