package de.pheromir.trustedbot.commands.base;

import java.awt.Color;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.pheromir.trustedbot.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;

public abstract class RandomImageCommand extends TrustedCommand {

	protected String BASE_URL;
	protected String jsonKey;

	public RandomImageCommand() {
		this.botPermissions = new Permission[] { Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS };
		this.help = "Shows a random picture.";
		this.guildOnly = true;
		this.category = new Category("Fun");
		this.cooldown = 10;
		this.cooldownScope = CooldownScope.USER_GUILD;
		this.nsfw = false;
	}

	@Override
	protected boolean exec(CommandEvent e) {
		try {
			Unirest.get(BASE_URL).asJsonAsync(new Callback<JsonNode>() {

				@Override
				public void completed(HttpResponse<JsonNode> response) {
					if(response.getStatus() != 200) {
						Main.LOG.error("RandomImage " + RandomImageCommand.this.name + " received HTTP Code " + response.getStatus());
						e.reply("An error occurred while getting a random Image");
						return;
					}
					String imgUrl = response.getBody().getObject().getString(jsonKey);
					e.reply(new EmbedBuilder().setImage(imgUrl).setColor(e.getChannelType() == ChannelType.TEXT
							? e.getSelfMember().getColor()
							: Color.BLUE).build());
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
		} catch (Exception e1) {
			Main.LOG.error("", e1);
			return false;
		}
	}

}
