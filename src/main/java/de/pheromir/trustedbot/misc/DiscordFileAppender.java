/*******************************************************************************
 * Copyright (C) 2019 Pheromir
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
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
			String throwable = event.getThrowableProxy() == null ? null
					: ThrowableProxyUtil.asString(event.getThrowableProxy());
			if (!event.getMessage().contains("disconnected from WebSocket. Attempting to resume")
					&& throwable.contains("de.pheromir")) {
				Main.exceptionAmount++;
				Main.jda.getUserById(Main.adminId).openPrivateChannel().queue(priv -> priv.sendMessage(String.format("**An error occurred:**\n"
						+ "%s [%s] %s %s %s", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(LocalDateTime.now()), event.getThreadName(), event.getLevel().levelStr, event.getMessage(), throwable == null
								? ""
								: "\n" + (throwable.length() > 1800 ? throwable.substring(0, 1797) + "..."
										: throwable))).queue());
			}

		}
	}

}
