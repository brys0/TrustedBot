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

import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

public class DJRemoveCommand extends TrustedCommand {

	public DJRemoveCommand() {
		this.name = "djremove";
		this.help = "Remove DJ permissions of the specified user.";
		this.arguments = "<User-Mention>";
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.guildOnly = true;
		this.category = new Category("Music");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		Pattern p = Pattern.compile("(\\d+)");
		for (String arg : args) {
			Matcher m = p.matcher(arg);
			if (m.find()) {
				String id = m.group(1);
				Member mem = e.getGuild().getMemberById(id);
				if (mem == null) {
					e.reply("The specified user couldn't be found.");
					continue;
				}
				if (!gc.getDJs().contains(Long.parseLong(id))) {
					e.reply(mem.getAsMention() + " is not a DJ.");
					continue;
				} else {
					e.reply(mem.getAsMention() + " is no longer a DJ.");
					gc.removeDJ(Long.parseLong(id));
					continue;
				}
			} else {
				e.reply("The specified user couldn't be found.");
				continue;
			}
		}
		return true;
	}
}
