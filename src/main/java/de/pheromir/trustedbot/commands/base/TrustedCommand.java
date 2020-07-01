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
package de.pheromir.trustedbot.commands.base;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;

public abstract class TrustedCommand extends Command {

    protected long creditsCost;
    protected boolean nsfw;
    private static String usageString = "Invalid Syntax. Usage: `%s%s %s`";

    public TrustedCommand() {
        this.creditsCost = 0;
    }

    @Override
    protected final void execute(CommandEvent e) {
        String[] args = e.getArgs().split(" ");
        if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
            args = new String[0];

        if (e.getGuild() == null) {
            exec(e, null, args, String.format(usageString, "@" + e.getSelfUser().getName()
                    + " ", this.name, this.arguments));
        } else {
            GuildConfig gc = Main.getGuildConfig(e.getGuild());

            if (gc.getBlacklist().contains(e.getAuthor().getIdLong())) {
                return;
            }

            if (e.getChannelType() == ChannelType.TEXT && gc.isCommandDisabled(this.name) && !this.isOwnerCommand()) {
                e.reply(Main.COMMAND_DISABLED);
                return;
            }

            if (nsfw && e.getChannelType() == ChannelType.TEXT && !((TextChannel) e.getChannel()).isNSFW()) {
                e.reply("This command can be used in NSFW-channels only.");
                return;
            }

            if (costsCredits() && gc.getUserCredits(e.getMember().getUser().getIdLong()) < this.getCreditCost()) {
                e.reply("You need at least " + creditsCost + (getCreditCost() == 1 ? " credit" : " credits")
                        + " to use this command.");
                return;
            }

            if (exec(e, gc, args, String.format(usageString, gc.getPrefix(), this.name, this.arguments))
                    && costsCredits()) {
                e.reply("- " + getCreditCost() + (getCreditCost() == 1 ? " credit" : " credits"));
                gc.setUserCredits(e.getMember().getUser().getIdLong(), gc.getUserCredits(e.getMember().getUser().getIdLong())
                        - this.getCreditCost());
            }
        }

    }

    protected abstract boolean exec(CommandEvent e, @Nullable GuildConfig gc, final String[] args, String usage);

    public boolean costsCredits() {
        return creditsCost != 0;
    }

    public long getCreditCost() {
        return creditsCost < 0 ? 0L : creditsCost;
    }

    public boolean isNSFW() {
        return nsfw;
    }
}
