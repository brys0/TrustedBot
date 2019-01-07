package de.pheromir.trustedbot.commands.base;

import java.awt.Color;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.exceptions.HttpErrorException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;

public abstract class RandomImageCommand extends Command {

	protected String BASE_URL;
	protected String jsonKey;

	public RandomImageCommand() {
		this.botPermissions = new Permission[] { Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS };
		this.help = "Shows a random picture.";
		this.guildOnly = false;
		this.category = new Category("Fun");
		this.cooldown = 60;
		this.cooldownScope = CooldownScope.USER_GUILD;
	}

	@Override
	protected void execute(CommandEvent e) {
		if(e.getChannelType() == ChannelType.TEXT && Main.getGuildConfig(e.getGuild()).isCommandDisabled(this.name)) {
			e.reply(Main.COMMAND_DISABLED);
			return;
		}
		String imgUrl;
		try {
			imgUrl = Methods.httpRequestJSON(BASE_URL).getString(jsonKey);
			e.reply(new EmbedBuilder().setImage(imgUrl).setColor(e.getChannelType() == ChannelType.TEXT
					? e.getSelfMember().getColor()
					: Color.BLUE).build());
		} catch (JSONException | HttpErrorException | InterruptedException | ExecutionException | TimeoutException e1) {
			e.reply("Es ist ein Fehler aufgetreten.");
			Main.exceptionAmount++;
			e1.printStackTrace();
		}
	}

}
