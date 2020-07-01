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

public class VolumeCommand extends TrustedCommand {

    public VolumeCommand() {
        this.name = "volume";
        this.arguments = "<1-100>";
        this.aliases = new String[]{"vol"};
        this.help = "Set the playback volume. (currently only for selected users available)";
        this.guildOnly = true;
        this.category = new Category("Music");
    }

    @Override
    protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
        if (args.length == 0) {
            e.reply("Current volume: " + gc.player.getVolume());
            return false;
        }
        if (!Main.getExtraUsers().contains(e.getAuthor().getIdLong())) {
            e.reply("For performance reasons, this command is only available for selected users. Sorry.\n"
                    + "You can control the volume in your discord-client by rightclicking me.");
            return false;
        }
        if (!gc.getDJs().contains(e.getAuthor().getIdLong())) {
            e.reply("You need DJ permissions to use this command.");
            return false;
        }

        int vol = 100;
        try {
            vol = Integer.parseInt(args[0]);
            if (vol < 1 || (vol > 100 && !e.getAuthor().getId().equals(Main.adminId))) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            e.reply("Invalid value. Please specify a value between 1-100.");
            return false;
        }
        gc.setVolume(vol);
        e.reactSuccess();
        return true;
    }

}
