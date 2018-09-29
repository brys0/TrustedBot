package de.pheromir.trustedbot.events;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.config.SettingsManager;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildLeave extends ListenerAdapter {

	@Override
	public void onGuildLeave(GuildLeaveEvent e) {
		Main.getGuildConfig(e.getGuild()).delete();
		SettingsManager.guildConfigs.remove(e.getGuild().getIdLong());
	}

}
