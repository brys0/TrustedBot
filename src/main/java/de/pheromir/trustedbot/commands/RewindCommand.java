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
import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.api.Permission;

public class RewindCommand extends TrustedCommand {

    public RewindCommand() {
        this.name = "rewind";
        this.arguments = "<HH:mm:ss>";
        this.help = "Rewind the current track by the specified time.";
        this.guildOnly = true;
        this.category = new Category("Music");
    }

    @Override
    protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
        if (!gc.getDJs().contains(e.getAuthor().getIdLong())
                && !e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            e.reply("You need DJ privileges to rewind the track.");
            return false;
        }

        if (args.length == 0) {
            e.reply(usage);
            return false;
        }

        long time;
        try {
            time = Methods.parseTimeString(e.getArgs());
        } catch (Exception ex) {
            e.reply(usage);
            return false;
        }
        if (gc.player.getPlayingTrack() == null) {
            e.reactError();
            return false;
        }
        time -= gc.player.getPlayingTrack().getDuration();

        gc.player.getPlayingTrack().setPosition(gc.player.getPlayingTrack().getDuration() < time
                ? gc.player.getPlayingTrack().getDuration()
                : (time < 0) ? 0 : time);
        e.reactSuccess();
        return true;
    }

}
