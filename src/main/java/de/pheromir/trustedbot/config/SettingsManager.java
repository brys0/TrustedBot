package de.pheromir.trustedbot.config;

import java.util.HashMap;

import com.jagrosh.jdautilities.command.GuildSettingsManager;

import net.dv8tion.jda.core.entities.Guild;

public class SettingsManager implements GuildSettingsManager<GuildConfig> {

	public static HashMap<Long, GuildConfig> guildConfigs = new HashMap<>();

	@Override
	public GuildConfig getSettings(Guild guild) {
		GuildConfig cfg = null;
		if (guildConfigs.containsKey(guild.getIdLong())) {
			cfg = guildConfigs.get(guild.getIdLong());
		} else {
			cfg = new GuildConfig(guild);
			guildConfigs.put(guild.getIdLong(), cfg);
		}
		return cfg;
	}

}
