package de.pheromir.trustedbot.commands.base;

import javax.annotation.Nullable;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.config.GuildConfig;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;

public abstract class TrustedCommand extends Command {

	protected long creditsCost;
	protected boolean nsfw;
	private static String usageString = "Invalid Syntax. Usage: `%s%s %s`";

	public TrustedCommand() {
		this.creditsCost = 0;
	}

	@Override
	protected final void execute(CommandEvent e) {
		String[] args = e.getArgs().split(" ");
		if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
			args = new String[0];

		if (e.getGuild() == null) {
			exec(e, null, args, String.format(usageString, "@" + e.getSelfUser().getName()
					+ " ", this.name, this.arguments));
		} else {
			GuildConfig gc = Main.getGuildConfig(e.getGuild());
			if (e.getChannelType() == ChannelType.TEXT && gc.isCommandDisabled(this.name) && !this.isOwnerCommand()) {
				e.reply(Main.COMMAND_DISABLED);
				return;
			}

			if (nsfw && e.getChannelType() == ChannelType.TEXT && !((TextChannel) e.getChannel()).isNSFW()) {
				e.reply("This command can be used in NSFW-channels only.");
				return;
			}

			if (costsCredits() && gc.getUserCredits(e.getMember().getUser().getIdLong()) < this.getCreditCost()) {
				e.reply("You need at least " + creditsCost + (getCreditCost() == 1 ? " credit" : " credits")
						+ " to use this command.");
				return;
			}

			if (exec(e, gc, args, String.format(usageString, gc.getPrefix(), this.name, this.arguments))
					&& costsCredits()) {
				e.reply("- " + getCreditCost() + (getCreditCost() == 1 ? " credit" : " credits"));
				gc.setUserCredits(e.getMember().getUser().getIdLong(), gc.getUserCredits(e.getMember().getUser().getIdLong())
						- this.getCreditCost());
			}
		}

	}

	protected abstract boolean exec(CommandEvent e, @Nullable GuildConfig gc, final String[] args, String usage);

	public boolean costsCredits() {
		return creditsCost != 0;
	}

	public long getCreditCost() {
		return creditsCost < 0 ? 0L : creditsCost;
	}

	public boolean isNSFW() {
		return nsfw;
	}
}
