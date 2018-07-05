package de.pheromir.discordmusicbot.commands;

import java.util.Map.Entry;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;

public class TokensCommand extends Command {
	
	public TokensCommand() {
		this.name = "tokens";
		this.help = "Liste mit allen Role-Tokens anzeigen";
		this.guildOnly = false;
	}

	@Override
	protected void execute(CommandEvent e) {
		User user = e.getAuthor();
		
		String roleString = "";
		for(Entry<Integer, String> ks : Main.roleTokenNames.entrySet()) {
			if(!Main.roleTokenTokens.containsKey(ks.getKey())) {
				Main.roleTokenNames.remove(ks.getKey());
			}
			roleString += "ID: `"+ks.getKey()+"`, Rolle: `"+ks.getValue()+"`, Token: `"+Main.roleTokenTokens.get(ks.getKey())+"`\n";
		}
		user.openPrivateChannel().complete().sendMessage("**Vorhandene Token:**\n"
        		+ roleString).complete();
		if(e.getChannelType() == ChannelType.TEXT) {
			e.reactSuccess();
		}
		return;
	}
	
	/*@Command(aliases = {"!tokens"}, description = "Role-Token einlösen", requiredPermissions = "bot.tokens")
    public String onCommand(String command, String[] args, TextChannel channel, ServerTextChannel sc, DiscordApi api, User user) {
		
    }*/
	
}
