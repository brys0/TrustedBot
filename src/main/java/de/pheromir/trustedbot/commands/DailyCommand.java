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

import java.util.Random;

public class DailyCommand extends TrustedCommand {

    public static long reward = 10L;

    public DailyCommand() {
        this.name = "daily";
        this.help = "Claim your daily reward";
        this.guildOnly = true;
        this.category = new Category("Money");

    }

    @Override
    protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
        if (gc.getRewardClaimed(e.getMember().getUser().getIdLong())) {
            e.reply("You already claimed your daily reward. Check back tomorrow!");
            return false;
        }
        e.reply("You claimed your daily reward and got " + reward + " credits.");
        gc.addRewardClaimed(e.getMember().getUser().getIdLong());
        long bonus = 0;
        if (new Random().nextInt(100) < 5) {
            bonus = 20;
            e.reply("BONUS: You're lucky and got a bonus of " + bonus + " credits. (5% chance)");
        }
        gc.setUserCredits(e.getMember().getUser().getIdLong(), gc.getUserCredits(e.getMember().getUser().getIdLong())
                + reward + bonus);
        return true;
    }

}
