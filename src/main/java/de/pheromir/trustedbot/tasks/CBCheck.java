package de.pheromir.trustedbot.tasks;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.config.GuildConfig;
import kong.unirest.Callback;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import net.dv8tion.jda.api.EmbedBuilder;

public class CBCheck implements Runnable {

    @Override
    public void run() {
        synchronized (GuildConfig.getCBList()) {
            Thread.currentThread().setName("CB-Task");
            for (String username : GuildConfig.getCBList().keySet()) {
                Main.LOG.debug("Checking Livecam " + username);
                String url = "https://chaturbate.com/" + username;
                Unirest.get(url).asStringAsync(response -> {
                    if(response.isSuccess()) {
                        String res = response.getBody();
                        if (Main.onlineCBList.contains(username)) {
                            if (res.contains("room_status\\u0022: \\u0022offline")) {
                                Main.onlineCBList.remove(username);
                            }
                        } else {
                            if (res.contains("room_status\\u0022: \\u0022public")) {
                                Main.onlineCBList.add(username);

                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle(username + " is now online!", url);
                                eb.setAuthor("Chaturbate");
                                eb.setThumbnail("https://ssl-ccstatic.highwebmedia.com/images/logo-standard.png");
                                eb.setImage("https://roomimg.stream.highwebmedia.com/ri/" + username + ".jpg?" + System.currentTimeMillis());

                                for (Long chId : GuildConfig.getCBList().get(username)) {
                                    Main.jda.getTextChannelById(chId).sendMessage(eb.build()).complete();
                                }
                            }
                        }
                    } else {
                        Main.LOG.error("CB-Check Error " + response.getStatus() + " " + response.getStatusText());
                    }
                });
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Main.LOG.error("", e);
                    continue;
                }
            }
        }
    }

    public static boolean doesCBUserExist(String username) {
        String res;
        res = Unirest.get("https://de.chaturbate.com/" + username).asString().getBody();
        return !res.contains("HTTP 404 - Seite nicht gefunden");
    }

}