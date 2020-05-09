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

import java.awt.Color;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.config.GuildConfig;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;

public abstract class RandomImageCommand extends TrustedCommand {

	protected String BASE_URL;
	protected String jsonKey;
	protected String interactText = null;
	protected String interactSelfText = null;

	public RandomImageCommand() {
		this.botPermissions = new Permission[] { Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS };
		this.help = "Shows a random picture.";
		this.guildOnly = false;
		this.category = new Category("Fun");
		this.cooldown = 5;
		this.cooldownScope = CooldownScope.USER_GUILD;
		this.nsfw = false;
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		Unirest.get(BASE_URL).asJsonAsync(new Callback<JsonNode>() {

			@Override
			public void completed(HttpResponse<JsonNode> response) {
				if (response.getStatus() != 200) {
					Main.LOG.error("RandomImage " + RandomImageCommand.this.name + " received HTTP Code "
							+ response.getStatus());
					e.reply("An error occurred while getting a random Image");
					return;
				}
				String imgUrl = response.getBody().getObject().getString(jsonKey);
				EmbedBuilder emb = new EmbedBuilder().setImage(imgUrl).setColor(e.getChannelType() == ChannelType.TEXT
						? e.getSelfMember().getColor()
						: Color.BLUE);

				if (interactText != null && interactSelfText != null) {
					if (e.getMessage().getMentionedMembers().size() != 0) {
						if (e.getMessage().getMentionedMembers().get(0) == e.getMember()) {
							emb.setDescription(String.format(interactSelfText, e.getMember().getAsMention()));
						} else {
							emb.setDescription(String.format(interactText, e.getMember().getAsMention(), e.getMessage().getMentionedMembers().get(0).getAsMention()));
						}
					}
				}

				e.reply(emb.build());
			}

			@Override
			public void failed(UnirestException e1) {
				Main.LOG.error("Getting RandomImage " + RandomImageCommand.this.name + " failed: ", e1);
				e.reply("An error occurred while getting a random Image");
			}

			@Override
			public void cancelled() {
				Main.LOG.error("Getting RandomImage " + RandomImageCommand.this.name + " cancelled.");
				e.reply("An error occurred while getting a random Image");
			}

		});
		return true;
	}
}
