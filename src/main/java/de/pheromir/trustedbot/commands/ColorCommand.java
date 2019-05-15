package de.pheromir.trustedbot.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import de.pheromir.trustedbot.ButtonMenu;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.Permission;

public class ColorCommand extends TrustedCommand {

	private final EventWaiter waiter;

	public ColorCommand(EventWaiter waiter) {
		this.name = "color";
		this.aliases = new String[] { "colorchooser", "colors" };
		this.help = "Creates a Message for users to choose a role-color using reactions";
		this.guildOnly = true;
		this.botPermissions = new Permission[] { Permission.MANAGE_ROLES };
		this.waiter = waiter;
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		ButtonMenu menu = new ButtonMenu.Builder().setChoices("\u2764", "\ud83d\udd36", "\ud83d\udc9b", "\ud83d\udc9a", "\ud83d\udc99", "\ud83d\udc9c", "\ud83d\udda4", "\u274C").setUsers(e.getAuthor()).setText("Choose a color for your name, "
				+ e.getMember().getAsMention()).setEventWaiter(this.waiter).setTimeout(5, TimeUnit.MINUTES).setAction(action -> {
					String roleName = "";
					Integer color = null;
					ArrayList<String> colors = new ArrayList<>();
					Collections.addAll(colors, "RED", "ORANGE", "YELLOW", "GREEN", "BLUE", "PURPLE", "BLACK");
					switch (action.getReactionEmote().getName()) {
						case "\u2764":
							roleName = "RED";
							color = 0xFF0000;
							break;
						case "\ud83d\udd36":
							roleName = "ORANGE";
							color = 0xFF7F00;
							break;
						case "\ud83d\udc9b":
							roleName = "YELLOW";
							color = 0xFFF200;
							break;
						case "\ud83d\udc9a":
							roleName = "GREEN";
							color = 0x00FF00;
							break;
						case "\ud83d\udc99":
							roleName = "BLUE";
							color = 0x00D4FF;
							break;
						case "\ud83d\udc9c":
							roleName = "PURPLE";
							color = 0xA644AA;
							break;
						case "\ud83d\udda4":
							roleName = "BLACK";
							color = 0x010101;
							break;
						case "\u274C":
							roleName = "CLEAR";
							color = 0xFFFFFF;
							break;
					}
					if (roleName == "" || color == null) {
						return;
					}
					action.getGuild().getController().removeRolesFromMember(action.getMember(), action.getMember().getRoles().stream().filter(rol -> colors.contains(rol.getName())).collect(Collectors.toList())).complete();
					if (color != 0xFFFFFF) {
						if (action.getGuild().getRolesByName(roleName, false).size() == 0) {
							action.getGuild().getController().createRole().setName(roleName).setColor(color).queue(r -> {
								action.getGuild().getController().addSingleRoleToMember(action.getMember(), r).queue(v -> {
								}, thr -> {
									e.reply("An error occurred: " + thr.getMessage());
								});
							}, thr -> {
								e.reply("An error occurred: " + thr.getMessage());
							});
						} else {
							action.getGuild().getController().addSingleRoleToMember(action.getMember(), action.getGuild().getRolesByName(roleName, false).get(0)).queue(v -> {
							}, thr -> {
								e.reply("An error occurred: " + thr.getMessage());
							});
						}
					}

				}).setFinalAction(m -> {
					m.delete().queueAfter(2, TimeUnit.SECONDS);
					e.getMessage().delete().queueAfter(2, TimeUnit.SECONDS);
				}).build();

		menu.display(e.getChannel());
		return true;
	}

}
