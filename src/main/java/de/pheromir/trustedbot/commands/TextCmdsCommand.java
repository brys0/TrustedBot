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

public class TextCmdsCommand extends TrustedCommand {

	public TextCmdsCommand() {
		this.name = "textcmds";
		this.help = "List all custom text-commands for the current guild.";
		this.guildOnly = true;
		this.category = new Category("Custom Commands");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (gc.getCustomCommands().size() < 1) {
			e.reply("There are currently no custom commands.");
			return false;
		}
		String cmds = "There are currently the following text-commands: ";
		StringBuilder sb = new StringBuilder();
		for (String str : gc.getCustomCommands().keySet()) {
			sb.append("`" + str + "`, ");
		}
		e.reply(cmds + sb.toString().substring(0, sb.length() - 2));
		return true;
	}
}
