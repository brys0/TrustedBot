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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

public class BlacklistCommand extends TrustedCommand {

	public BlacklistCommand() {
		this.name = "blacklist";
		this.help = "(Un-)Blacklist a user from using the whole Bot.";
		this.arguments = "<User-Mention>";
		this.aliases = new String[] {"bl"};
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.guildOnly = true;
		this.category = new Category("Settings");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (args.length == 0) {
			ArrayList<Long> blacklist = (ArrayList<Long>) gc.getBlacklist();
			if (blacklist.isEmpty()) {
				e.reply("The blacklist is empty.");
				return false;
			} else {
				StringBuilder sb = new StringBuilder();
				for (Long id : blacklist) {
					String temp = "";
					if (gc.getGuild().getMemberById(id) != null)
						temp = gc.getGuild().getMemberById(id).getUser().getAsTag();
					else
						temp = Long.toString(id);
					sb.append("`" + temp + "`, ");
				}
				String msg = sb.substring(0, sb.length() - 2);
				e.reply("Blacklisted Users (or IDs if they left): " + msg);
				return false;
			}
		}
		if (args.length != 1) {
			e.reply(usage);
			return false;
		} else {
			Pattern p = Pattern.compile("(\\d+)");
			ArrayList<Long> blacklist = (ArrayList<Long>) gc.getBlacklist();
			for (String arg : args) {
				Matcher m = p.matcher(arg);
				if (m.find()) {
					String idStr = m.group(1);
					Long id = Long.parseLong(idStr);
					if (blacklist.contains(id)) {
						String temp = "";
						if (gc.getGuild().getMemberById(id) != null)
							temp = gc.getGuild().getMemberById(id).getUser().getAsTag();
						else
							temp = Long.toString(id);
						e.reply(temp + " is now unblacklisted.");
						gc.removeFromBlacklist(id);
					} else {
						if (id.toString().equals(Main.adminId)) {
							e.reply("You can't blacklist that user.");
							continue;
						} else if (id.equals(e.getAuthor().getIdLong())) {
							e.reply("You can't blacklist yourself.");
							continue;
						}
						Member mem = e.getGuild().getMemberById(id);
						if (mem == null) {
							e.reply("The specified user couldn't be found or is not on this server.");
							continue;
						}
						e.reply(mem.getUser().getAsTag() + " is now blacklisted.");
						gc.addToBlacklist(id);
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

}
