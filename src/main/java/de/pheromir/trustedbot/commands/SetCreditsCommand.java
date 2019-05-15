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

public class SetCreditsCommand extends TrustedCommand {

	public SetCreditsCommand() {
		this.name = "setcredits";
		this.help = "Set the credits of the specified user";
		this.guildOnly = true;
		this.arguments = "<UserID> <Credits>";
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.category = new Category("Money");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (args.length != 2) {
			e.reply(usage);
			return false;
		}
		Pattern p = Pattern.compile("(\\d+)");
		Matcher m = p.matcher(args[0]);
		if (m.find()) {
			String id = m.group(1);
			Member mem = e.getGuild().getMemberById(id);
			if (mem == null) {
				e.reply("The specified user couldn't be found.");
				return false;
			}
			long amount;
			try {
				amount = Long.parseLong(args[1]);
				if (amount < 0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e1) {
				e.reply("Invalid amount.");
				return false;
			}
			gc.setUserCredits(mem.getUser().getIdLong(), amount);
			e.reply(mem.getEffectiveName() + "'s credits have been set to " + amount + ".");
			return true;
		} else {
			e.reply("The specified user couldn't be found.");
			return false;
		}

	}
}
