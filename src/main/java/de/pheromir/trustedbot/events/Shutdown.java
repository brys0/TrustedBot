package de.pheromir.trustedbot.events;

import java.io.IOException;

import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Shutdown extends ListenerAdapter {

	@Override
	public void onShutdown(ShutdownEvent e) {
		try {
			Unirest.shutdown();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
