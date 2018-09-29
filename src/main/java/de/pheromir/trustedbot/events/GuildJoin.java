package de.pheromir.trustedbot.events;

import de.pheromir.trustedbot.Main;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildJoin extends ListenerAdapter {

	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		Main.getGuildConfig(e.getGuild());
	}

}
