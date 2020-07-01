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

public class CoinflipCommand extends TrustedCommand {

    public CoinflipCommand() {
        this.name = "cf";
        this.aliases = new String[]{"coinflip"};
        this.arguments = "<heads|tails> <stake>";
        this.help = "Flip a coin to win credits.";
        this.guildOnly = true;
        this.category = new Category("Minigames and Gambling");
        this.cooldown = 5;
        this.cooldownScope = CooldownScope.USER_GUILD;
    }

    @Override
    protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
        if (args.length != 2 || !args[0].toLowerCase().matches("^(heads|tails)$")) {
            e.reply(usage);
            return false;
        }

        long stake;
        try {
            stake = Long.parseLong(args[1]);
        } catch (NumberFormatException e1) {
            e.reply("Please enter a valid number.");
            return false;
        }

        if (stake > gc.getUserCredits(e.getAuthor().getIdLong())) {
            e.reply("You don't have enough credits.");
            return false;
        }

        // heads = true, tails = false
        boolean chosenSide = args[0].equalsIgnoreCase("heads") ? true : false;
        boolean random = new Random().nextBoolean();
        if (random == chosenSide) {
            long wonCredits = (long) Math.ceil((stake * 0.5));
            e.reply((random ? "Heads!" : "Tails!") + " You've won, " + e.getAuthor().getAsMention() + "! Your prize: "
                    + wonCredits + " Credits.");
            gc.setUserCredits(e.getAuthor().getIdLong(), gc.getUserCredits(e.getAuthor().getIdLong()) + wonCredits);
        } else {
            e.reply((random ? "Heads!" : "Tails!") + " You've lost your stake, " + e.getAuthor().getAsMention() + ".");
            gc.setUserCredits(e.getAuthor().getIdLong(), gc.getUserCredits(e.getAuthor().getIdLong()) - stake);
        }

        return true;
    }

}
