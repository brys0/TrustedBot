package de.pheromir.trustedbot.events;

import java.io.IOException;

import com.mashape.unirest.http.Unirest;

import de.pheromir.trustedbot.Main;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Shutdown extends ListenerAdapter {

	@Override
	public void onShutdown(ShutdownEvent e) {
		try {
			Unirest.shutdown();
			System.exit(0);
		} catch (IOException e1) {
			Main.LOG.error("", e);
		}
	}

}
