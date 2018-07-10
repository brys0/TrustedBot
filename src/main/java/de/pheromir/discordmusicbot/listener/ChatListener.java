package de.pheromir.discordmusicbot.listener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ChatListener extends ListenerAdapter {

	private HashMap<Member, Integer> brbAmount = new HashMap<>();

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if (e.getMessage() == null)
			return;
		if (e.getMember() == null)
			return;
		if (e.getMember().getUser() != e.getJDA().getSelfUser() && e.getGuild().getId().equals("392063800393728021")
				&& (e.getMessage().getContentDisplay().contains("brb")
						|| e.getMessage().getContentDisplay().contains("brb"))
				&& !e.getMessage().getContentDisplay().startsWith("!")) {
			if (!brbAmount.containsKey(e.getMember())) {
				brbAmount.put(e.getMember(), 1);
			} else {
				brbAmount.put(e.getMember(), brbAmount.get(e.getMember()) + 1);
			}
			int amt = brbAmount.get(e.getMember());
			if (amt == 1) {
				e.getChannel().sendMessage("Verzieh dich mit deinem ekeligen "
						+ (e.getMessage().getContentDisplay().contains("brb") ? "brb" : "bbl") + "!").queue();
			} else if (amt == 2) {
				e.getChannel().sendMessage("Provoziere mich nicht, " + e.getMember().getAsMention() + "!").queue();
			} else {
				e.getChannel().sendMessage(e.getMember().getAsMention()
						+ " lernt wohl nicht aus seinen Fehlern. Adieu!").queue();
				brbAmount.remove(e.getMember());
				if (e.getGuild().getMember(e.getJDA().getSelfUser()).canInteract(e.getMember())) {
					e.getGuild().getController().kick(e.getMember(), "be right back").queue();
				}
			}
		}

		if (e.getMember().getUser() == e.getJDA().getSelfUser() && e.getMessage().getContentDisplay().contains("giphy")
				|| e.getMessage().getContentDisplay().contains("!random")) {
			e.getMessage().delete().queueAfter(5, TimeUnit.MINUTES);
		} else if (e.getMember().getUser() == e.getJDA().getSelfUser()
				&& e.getMessage().getContentDisplay().contains("**Titelauswahl:**")) {
			e.getMessage().delete().queueAfter(2, TimeUnit.MINUTES);
		}

	}

}
