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
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.api.Permission;

public class TextCmdAddCommand extends TrustedCommand {

    public TextCmdAddCommand() {
        this.name = "textcmdadd";
        this.help = "Create a custom text-command.";
        this.arguments = "<command> <response>";
        this.guildOnly = true;
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.category = new Category("Custom Commands");
    }

    @Override
    protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
        if (args.length < 2) {
            e.reply(usage);
            return false;
        }

        String name = args[0].toLowerCase();
        if (Main.commandClient.getCommands().stream().anyMatch(c -> c.isCommandFor(name))
                || gc.getAliasCommands().containsKey(name)) {
            e.reply("There is already a command with that name.");
            return false;
        }
        String arguments = "";
        if (args.length >= 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i] + " ");
            }
            arguments = sb.toString().trim();
        }
        if (gc.getCustomCommands().containsKey(name)) {
            e.reply("The text-command `" + name + "` has been replaced.");
        } else {
            e.reply("The text-command `" + name + "` has been created.");
        }
        gc.addCustomCommand(name, arguments);
        return true;
    }
}
