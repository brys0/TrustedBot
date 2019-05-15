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

import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;

public class StatusCommand extends TrustedCommand {

	public StatusCommand() {
		this.name = "status";
		this.help = "Set the status of the bot. (is playing/listening..)";
		this.arguments = "<play|watch|listen|[stream]> [stream-url] <Text>";
		this.guildOnly = false;
		this.ownerCommand = true;
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		JDA api = e.getJDA();
		if (args.length < 2) {
			e.reply(usage);
			return false;
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = args[0].equalsIgnoreCase("stream") ? 2 : 1; i < args.length; i++) {
				sb.append(args[i] + " ");
			}
			String msg = sb.toString().trim();
			switch (args[0].toLowerCase()) {
				case "play":
					api.getPresence().setGame(Game.playing(msg));
					break;
				case "watch":
					api.getPresence().setGame(Game.watching(msg));
					break;
				case "listen":
					api.getPresence().setGame(Game.listening(msg));
					break;
				case "stream":
					api.getPresence().setGame(Game.streaming(msg, args[1]));
					break;
				default:
					e.reply(usage);
					return false;
			}
			e.reactSuccess();
			return true;
		}
	}

}
