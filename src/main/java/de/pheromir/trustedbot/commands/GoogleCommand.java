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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;

public class GoogleCommand extends TrustedCommand {

	private final String BASE_URL = "https://www.google.com/search?q=%s";

	public GoogleCommand() {
		this.name = "google";
		this.aliases = new String[] { "g" };
		this.botPermissions = new Permission[] { Permission.MESSAGE_WRITE };
		this.help = "Create a google search url with the specified keywords.";
		this.guildOnly = false;
		this.category = new Category("Miscellaneous");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		String url;
		try {
			url = String.format(BASE_URL, URLEncoder.encode(e.getArgs(), "UTF-8"));
			e.reply(url);
			return true;
		} catch (UnsupportedEncodingException e1) {
			Main.LOG.error("", e1);
			e.reply("Oops, looks like something went wrong.");
			return false;
		}
	}

}
