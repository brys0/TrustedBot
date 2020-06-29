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

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;

public class StatsCommand extends TrustedCommand {

	public StatsCommand() {
		this.name = "stats";
		this.help = "Shows current statistics of the bot";
		this.guildOnly = false;
		this.ownerCommand = true;
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		String ans = "Uptime: " + Methods.getTimeString(System.currentTimeMillis() - Main.startMillis, true) + "\n"
				+ "Guilds: " + e.getJDA().getGuilds().size() + "\n" 
				+ "Exceptions (WARN or higher): " + Main.exceptionAmount + "\n" 
				+ "Max Memory: " + Runtime.getRuntime().maxMemory() / 1024L / 1024L + " MiB\n"
				+ "Reserved Memory: " + Runtime.getRuntime().totalMemory() / 1024L / 1024L + " MiB\n" 
				+ "Free Memory: " + Runtime.getRuntime().freeMemory() / 1024L / 1024L + " MiB";
		e.reply(ans);
		return true;
	}

}
