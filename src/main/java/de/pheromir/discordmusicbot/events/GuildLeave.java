package de.pheromir.discordmusicbot.events;

import de.pheromir.discordmusicbot.Main;
import de.pheromir.discordmusicbot.config.SettingsManager;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildLeave extends ListenerAdapter {
	
	@Override
	public void onGuildLeave(GuildLeaveEvent e) {
		Main.getGuildConfig(e.getGuild()).delete();
		SettingsManager.guildConfigs.remove(e.getGuild().getIdLong());
	}
	
}
