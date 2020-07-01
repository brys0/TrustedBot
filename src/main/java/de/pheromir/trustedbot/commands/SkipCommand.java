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
import net.dv8tion.jda.api.Permission;

public class SkipCommand extends TrustedCommand {

    public SkipCommand() {
        this.name = "skip";
        this.help = "Skip the current track.";
        this.guildOnly = true;
        this.category = new Category("Music");
    }

    @Override
    protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
        if (args.length == 0) {
            if (!gc.getDJs().contains(e.getAuthor().getIdLong()) && gc.scheduler.getCurrentRequester() != e.getAuthor()
                    && !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                e.reply("You can only skip tracks that you requested.");
                return false;
            }
            gc.scheduler.nextTrack();
            e.reactSuccess();
            return true;
        } else {
            int index = Integer.MAX_VALUE;
            try {
                index = Integer.parseInt(e.getArgs()) - 1;
            } catch (NumberFormatException ex) {
                e.reply("Please enter a number between 1 - " + (gc.scheduler.getRequestedTitles().size() + 1) + ".");
                return false;
            }
            if (index >= gc.scheduler.getRequestedTitles().size() || index < 0) {
                e.reply("Please enter a number between 1 - " + (gc.scheduler.getRequestedTitles().size() + 1) + ".");
                return false;
            }
            if (!gc.getDJs().contains(e.getAuthor().getIdLong()) && !e.isOwner()
                    && gc.scheduler.getRequestedTitles().get(index).getRequestor() != e.getAuthor()
                    && !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                e.reply("You can only skip tracks that you requested.");
                return false;
            }
            String title = gc.scheduler.getRequestedTitles().get(index).getTrack().getInfo().title;
            if (gc.scheduler.skipTrackNr(index)) {
                e.reply("`" + title + "` skipped.");
                return true;
            } else {
                e.reply("Oops, looks like something went wrong.");
                return false;
            }
        }
    }

}
