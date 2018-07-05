package de.pheromir.discordmusicbot.commands;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.discordmusicbot.Main;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class TokenCommand extends Command {

	// @Command(aliases = { "!token" }, description = "Role-Token einlösen")
	// public String onCommand(String command, String[] args, TextChannel
	// channel, ServerTextChannel sc, DiscordApi api, User user, Message msg) {
	//
	// }

	public TokenCommand() {
		this.name = "token";
		this.guildOnly = false;
		this.help = "Role-Token einlösen";
		this.arguments = "<Token>";
	}

	@Override
	protected void execute(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		JDA api = e.getJDA();
		Message msg = e.getMessage();
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		if (e.getChannelType() == ChannelType.TEXT) {
			msg.delete().complete();
			e.reply("Bitte führe diesen Befehl aus Sicherheit im Privatchat mit mir aus.");
			return;
		}
		if (Main.serverID.equals("none")) {
			e.reply("**Fehler**: `ServerID in Konfiguration fehlt. Bitte wende dich an den Bot-Admin.`");
			return;
		}
		if (args.length != 1) {
			e.reply("**Verwendung**:\n" + "`!token <Token>`");
			return;
		}
		int roleID = -1;
		for (Entry<Integer, String> ent : Main.roleTokenTokens.entrySet()) {
			if (!Main.roleTokenNames.containsKey(ent.getKey())) {
				Main.roleTokenTokens.remove(ent.getKey());
				continue;
			}
			if (ent.getValue().equals(args[0])) {
				roleID = ent.getKey();
			}
		}
		if (roleID == -1) {
			e.reply("**Fehler**: `Der Token wurde nicht gefunden.`");
			return;
		}
		String roleName = Main.roleTokenNames.get(roleID);
		List<Role> rolesGen = api.getGuildById(Main.serverID).getRolesByName(roleName, true);
		if (rolesGen.isEmpty()) {
			e.reply("**Fehler**: `Die Gruppe, für die dieser Token generiert wurde, existiert nicht. Bitte wende dich an den Bot-Admin.`");
			return;
		}
		Role role = api.getGuildById(Main.serverID).getRolesByName(roleName, true).get(0);
		// Collection<Role> roles =
		// user.getRoles(api.getGuildById(Main.serverID));
		Collection<Role> roles = api.getGuildById(Main.serverID).getMember(e.getAuthor()).getRoles();
		if (roles.contains(role)) {
			e.reply("Du bist bereits in der Gruppe `" + role.getName() + "`.");
			return;
		}
		api.getGuildById(Main.serverID).getController().addSingleRoleToMember(api.getGuildById(Main.serverID).getMember(e.getAuthor()), role).complete();
		e.reply("Du wurdest zur Gruppe `" + role.getName() + "` hinzugefügt.");
		return;

	}

}
