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
package de.pheromir.trustedbot.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.ButtonMenu;

import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class ColorCommand extends TrustedCommand {

	private final EventWaiter waiter;

	public ColorCommand(EventWaiter waiter) {
		this.name = "color";
		this.aliases = new String[] { "colorchooser", "colors" };
		this.help = "Creates a Message for users to choose a role-color using reactions";
		this.guildOnly = true;
		this.botPermissions = new Permission[] { Permission.MANAGE_ROLES };
		this.waiter = waiter;
		this.category = new Category("Miscellaneous");
	}

	@Override
	protected boolean exec(CommandEvent e, GuildConfig gc, String[] args, String usage) {
		final Guild g = e.getGuild();
		final Member m = e.getMember();
		ButtonMenu menu = new ButtonMenu.Builder().setChoices("\u2764", "\ud83d\udd36", "\ud83d\udc9b", "\ud83d\udc9a", "\ud83d\udc99", "\ud83d\udc9c", "\ud83d\udda4", "\u274C").setUsers(e.getAuthor()).setText("Choose a color for your name, "
				+ e.getMember().getAsMention()).setEventWaiter(this.waiter).setTimeout(5, TimeUnit.MINUTES).setAction(action -> {
					String roleName = "";
					Integer color = null;
					ArrayList<String> colors = new ArrayList<>();
					Collections.addAll(colors, "RED", "ORANGE", "YELLOW", "GREEN", "BLUE", "PURPLE", "BLACK");
					switch (action.getEmoji()) {
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
					m.getRoles().stream().filter(rol -> colors.contains(rol.getName())).forEach(rol -> g.removeRoleFromMember(m, rol));
					if (color != 0xFFFFFF) {
						if (g.getRolesByName(roleName, false).size() == 0) {
							g.createRole().setName(roleName).setColor(color).queue(r -> {
								g.addRoleToMember(m, r).queue(v -> {
								}, thr -> {
									e.reply("An error occurred: " + thr.getMessage());
								});
							}, thr -> {
								e.reply("An error occurred: " + thr.getMessage());
							});
						} else {
							g.addRoleToMember(m, g.getRolesByName(roleName, false).get(0)).queue(v -> {
							}, thr -> {
								e.reply("An error occurred: " + thr.getMessage());
							});
						}
					}

				}).setFinalAction(mes -> {
					mes.delete().queueAfter(2, TimeUnit.SECONDS);
					e.getMessage().delete().queueAfter(2, TimeUnit.SECONDS);
				}).build();

		menu.display(e.getChannel());
		return true;
	}

}
