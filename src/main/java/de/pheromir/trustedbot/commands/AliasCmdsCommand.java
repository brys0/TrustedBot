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

public class AliasCmdsCommand extends TrustedCommand {

	public AliasCmdsCommand() {
		this.name = "aliases";
		this.help = "List all aliases for the current guild.";
		this.guildOnly = true;
		this.category = new Category("Command Aliases");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (gc.getAliasCommands().size() < 1) {
			e.reply("There are currently no aliases.");
			return false;
		}

		String cmds = "There are currently the following aliases: ";
		StringBuilder sb = new StringBuilder();
		for (String str : gc.getAliasCommands().keySet()) {
			sb.append("`" + str + "`, ");
		}
		e.reply(cmds + sb.toString().substring(0, sb.length() - 2));
		return true;
	}
}
