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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;

import net.dv8tion.jda.api.Permission;

public class ToggleCommand extends TrustedCommand {

	public ToggleCommand() {
		this.name = "toggle";
		this.help = "Enable/Disable a command for the guild.";
		this.arguments = "<Command>";
		this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
		this.guildOnly = true;
		this.category = new Category("Settings");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		if (args.length == 0) {
			if (gc.getDisabledCommands().isEmpty()) {
				e.reply("There is currently no command disabled.");
				return false;
			} else {
				StringBuilder sb = new StringBuilder();
				for (String str : gc.getDisabledCommands()) {
					sb.append("`" + str + "`, ");
				}
				String msg = sb.substring(0, sb.length() - 2);
				e.reply("These commands are currently disabled: " + msg);
				return false;
			}
		}
		if (args.length != 1) {
			e.reply(usage);
			return false;
		} else {
			Command c = Main.commandClient.getCommands().stream().filter(cmd -> cmd.isCommandFor(args[0])).findAny().orElse(null);
			if (c == null && !gc.getCustomCommands().containsKey(args[0].toLowerCase())
					&& !gc.getAliasCommands().containsKey(args[0].toLowerCase())) {
				e.reply("You can't disable a non-existant command.");
				return false;
			}
			if (((c == null) ? args[0] : c.getName()).equalsIgnoreCase("toggle") || (c != null && c.isOwnerCommand())) {
				e.reply("You can't disable this command.");
				return false;
			}
			if (gc.getDisabledCommands().contains(((c == null) ? args[0] : c.getName()))) {
				gc.enableCommand(((c == null) ? args[0] : c.getName()));
				e.reply("The command `" + ((c == null) ? args[0] : c.getName()) + "` is now enabled.");
				return true;
			} else {
				gc.disableCommand((c == null) ? args[0] : c.getName());
				e.reply("The command `" + ((c == null) ? args[0] : c.getName()) + "` is now disabled.");
				return true;
			}
		}
	}

}
