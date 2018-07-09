package de.pheromir.discordmusicbot.commands.base;

import java.awt.Color;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Methods;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;

public  abstract class RandomImageCommand extends Command {
	protected String BASE_URL;
	protected String jsonKey;
	
	
	public RandomImageCommand() {
		this.name = "";
		this.botPermissions = new Permission[] { Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS };
		this.help = "Zeigt ein zufälliges Bild";
		this.guildOnly = false;
		this.category = new Category("RandomImage");
	}
	
	@Override
	protected void execute(CommandEvent e) {
		String imgUrl = Methods.httpRequest(BASE_URL).getString(jsonKey);
		e.reply(new EmbedBuilder().setImage(imgUrl).setColor(e.getChannelType() == ChannelType.TEXT ? e.getSelfMember().getColor() : Color.BLUE).build());
	}

}
