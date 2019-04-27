package de.pheromir.trustedbot.misc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.rolling.RollingFileAppender;
import de.pheromir.trustedbot.Main;

public class DiscordFileAppender extends RollingFileAppender<ILoggingEvent> {

	@Override
	protected void append(ILoggingEvent event) {
		super.append(event);
		if (event.getLevel().isGreaterOrEqual(Level.WARN)) {
			if (!event.getMessage().contains("disconnected from WebSocket. Attempting to resume")) {
				Main.exceptionAmount++;
				String throwable = event.getThrowableProxy() == null ? null : ThrowableProxyUtil.asString(event.getThrowableProxy());
				Main.jda.getUserById(Main.adminId).openPrivateChannel().queue(priv -> priv.sendMessage(String.format("**An error occurred:**\n"
						+ "%s [%s] %s %s %s", 
						DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(LocalDateTime.now()), 
						event.getThreadName(), 
						event.getLevel().levelStr, 
						event.getMessage(), 
						throwable == null ? "" : "\n" + (throwable.length() > 1800 ? throwable.substring(0, 1797) + "..." : throwable)
						)).queue());
			}
			
		}
	}

}
