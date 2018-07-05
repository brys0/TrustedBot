package de.pheromir.discordmusicbot.commands;

import java.util.List;
import java.util.Random;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

public class AddTokenCommand extends Command {

	public AddTokenCommand() {
		this.name = "addtoken";
		this.help = "Role-Token hinzufügen";
		this.guildOnly = false;
		this.userPermissions = new Permission[] { Permission.MANAGE_ROLES };
		this.arguments = "<Rolle> [Token]";
	}

	@Override
	protected void execute(CommandEvent e) {
		// public String onCommand(String command, String[] args, TextChannel
		// channel, ServerTextChannel sc, DiscordApi api, User user, Message
		// msg) {
		
		String[] args = e.getArgs().split(" ");
		JDA api = e.getJDA();
		User user = e.getAuthor();
		Message msg = e.getMessage();
		if((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		if (args.length != 1 && args.length != 2) {
			e.reply("**Verwendung**:\n" + "`!addtoken <Rolle> <Token>` -> <Token> für <Rolle> erstellen\n" + "`!addtoken <Rolle>` -> Zufälligen Token für <Rolle> erstellen");
			return;
		}
		int id = getFreeID();
		String roleP = args[0];
		List<Role> roles = api.getGuildById(Main.serverID).getRoles();
		Role role = null;
		for (Role r : roles) {
			if (r.getName().toLowerCase().contains(roleP.toLowerCase())) {
				role = r;
				break;
			}
		}
		if (role == null) {
			e.reply("**Fehler**: `Es wurde keine Gruppe gefunden, die " + roleP + " beinhaltet.`");
			return;
		}
		String roleName = role.getName();
		String token = "";
		if (args.length == 2) {
			token = args[1];
			if (Main.roleTokenTokens.containsValue(token)) {
				e.reactError();
				e.reply("**Fehler**: `Dieser Token existiert bereits.`");
				return;
			}
		} else {
			token = getRandomToken(25);
		}
		Main.roleTokenNames.put(id, roleName);
		Main.roleTokenTokens.put(id, token);
		Main.saveRoleTokens();
		user.openPrivateChannel().complete().sendMessage("**Token erstellt!**\n" + "ID: `" + id + "`\n" + "Rolle: `" + roleName + "`\n" + "Token: `" + token + "`").complete();
		if (e.getChannelType() == ChannelType.PRIVATE) {
			return;
		} else {
			msg.delete();
			e.reactSuccess();
			return;
		}
		// }

	}

	public int getFreeID() {
		int id = -1;
		int i = 0;
		while (id == -1) {
			if (!Main.roleTokenNames.containsKey(i) && !Main.roleTokenTokens.containsKey(i)) {
				id = i;
				break;
			}
			i++;
		}
		return id;
	}

	String getRandomToken(int length) {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < length) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		if (Main.roleTokenTokens.containsValue(saltStr)) {
			return getRandomToken(length);
		}
		return saltStr;

	}
}
