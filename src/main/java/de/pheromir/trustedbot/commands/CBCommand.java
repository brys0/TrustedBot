package de.pheromir.trustedbot.commands;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import de.pheromir.trustedbot.Main;
import de.pheromir.trustedbot.Methods;
import de.pheromir.trustedbot.commands.base.TrustedCommand;
import de.pheromir.trustedbot.config.GuildConfig;
import de.pheromir.trustedbot.tasks.CBCheck;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import org.jetbrains.annotations.Nullable;

public class CBCommand extends TrustedCommand {

    public CBCommand() {
        this.name = "cb";
        this.help = "Enable/Disable notifications if the specified camgirl of chaturbate.com goes live.";
        this.arguments = "[Username]";
        this.userPermissions = new Permission[] { Permission.ADMINISTRATOR };
        this.guildOnly = true;
        this.hidden = true;
        this.category = new Category("Subscriptions");
    }

    @Override
    protected boolean exec(CommandEvent e, @Nullable GuildConfig gc, String[] args, String usage) {
        if(e.getChannelType() == ChannelType.TEXT && Main.getGuildConfig(e.getGuild()).isCommandDisabled(this.name)) {
            e.reply(Main.COMMAND_DISABLED);
            return false;
        }
        if ((args[0].equals("") || args[0].isEmpty()) && args.length == 1)
            args = new String[0];

        if (args.length == 0) {
            ArrayList<String> streams = (ArrayList<String>) GuildConfig.getCBList().keySet().stream().filter(k -> GuildConfig.getCBList().get(k).contains(e.getChannel().getIdLong())).collect(Collectors.toList());
            if (streams.isEmpty()) {
                e.reply("There are currently no notifications active for this channel.");
                return false;
            } else {
                StringBuilder sb = new StringBuilder();
                for (String str : streams) {
                    sb.append("`" + str + "`, ");
                }
                String msg = sb.substring(0, sb.length() - 2);
                e.reply("There are currently the following notifications active: " + msg);
                return false;
            }
        }
        if (args.length != 1) {
            e.reply("Syntaxerror. Usage: `!" + this.name + " [username]`");
            return false;
        } else {
            if (GuildConfig.getCBList().containsKey(e.getArgs().toLowerCase())
                    && GuildConfig.getCBList().get(e.getArgs().toLowerCase()).contains(e.getChannel().getIdLong())) {
                GuildConfig.removeCBStream(e.getArgs().toLowerCase(), e.getChannel().getIdLong());
                e.reply("CB-notifications for " + e.getArgs().toLowerCase()
                        + " have been disabled in this channel.");
            } else {
                if (!CBCheck.doesCBUserExist(e.getArgs())) {
                    e.reply("There doesn't seem to be a user with this name (or an error occurred)");
                    return false;
                }
                GuildConfig.addCBStream(e.getArgs().toLowerCase(), e.getChannel().getIdLong(), e.getGuild().getIdLong());
                e.reply("CB-notifications for " + e.getArgs().toLowerCase()
                        + " have been enabled in this channel.");
            }
            return false;
        }
    }

    protected void exec(CommandEvent e) {

    }

}