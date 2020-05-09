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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;

import net.dv8tion.jda.api.Permission;

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
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (args.length != 1) {
			e.reply(usage);
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
						for (String trackUrl : response.getBody().split("\\r?\\n")) {
							PlayCommand.loadTrack(e, trackUrl, false);
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
