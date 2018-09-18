package de.pheromir.discordmusicbot.events;

import de.pheromir.discordmusicbot.Main;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildJoin extends ListenerAdapter {

	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		Main.getGuildConfig(e.getGuild());
	}

}
