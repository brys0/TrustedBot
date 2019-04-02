package de.pheromir.trustedbot.commands.base;

import java.awt.Color;

import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.Methods;
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
		String imgUrl;
		try {
			imgUrl = Methods.httpRequestJSON(BASE_URL).getString(jsonKey);
			e.reply(new EmbedBuilder().setImage(imgUrl).setColor(e.getChannelType() == ChannelType.TEXT
					? e.getSelfMember().getColor()
					: Color.BLUE).build());
		} catch (Exception e1) {
			Main.LOG.error("", e1);
		}
	}

}
