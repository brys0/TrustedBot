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
package de.pheromir.trustedbot.commands.images;

import com.jagrosh.jdautilities.command.CommandEvent;
import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.commands.base.RandomImageCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import kong.unirest.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;

import java.awt.*;

public class DogeCommand extends RandomImageCommand {

    public DogeCommand() {
        this.name = "shiba";
        this.aliases = new String[]{"doge"};
        this.help = "Shows a random shiba picture.";
        this.BASE_URL = "https://shibe.online/api/shibes?count=1";
        this.jsonKey = "url";
        this.isOnlyArray = true;
    }
}
