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
import kong.unirest.Callback;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import net.dv8tion.jda.api.Permission;

public class NumberFactCommand extends TrustedCommand {

    private final String BASE_URL = "http://numbersapi.com/{n}";

    public NumberFactCommand() {
        this.name = "numberfact";
        this.aliases = new String[]{"nf"};
        this.arguments = "<Integer>";
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.help = "Shows a fact about the given number.";
        this.guildOnly = false;
        this.category = new Category("Miscellaneous");
        this.cooldown = 10;
        this.cooldownScope = CooldownScope.USER_GUILD;
    }

    @Override
    protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
        if (args.length == 0) {
            e.reply(usage);
            return false;
        }
        int n;
        try {
            n = Integer.parseInt(e.getArgs());
            if (n < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e1) {
            e.reply(usage);
            return false;
        }
        Unirest.get(BASE_URL).routeParam("n", n + "").asStringAsync(new Callback<String>() {

            @Override
            public void cancelled() {
                e.reply("An error occured while getting your fact.");
            }

            @Override
            public void completed(HttpResponse<String> response) {
                if (response.getStatus() != 200) {
                    e.reply("An error occurred while getting your fact.");
                    Main.LOG.error("NumberFact received a HTTP Code " + response.getStatus());
                    return;
                }
                e.reply(response.getBody());
            }

            @Override
            public void failed(UnirestException arg0) {
                e.reply("An error occured while getting your fact.");
            }

        });
        return true;
    }

}
