/*
 * Copyright (c) 2019 Cerus
 * File created at 13.04.19 23:59
 * Last modification: 13.04.19 23:59
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.util.BotChannelUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class VoteUCommand extends UserCommand {
    public VoteUCommand() {
        super("vote");
        setDescription("Shows the vote link and some information.");
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if (!BotChannelUtil.isBotChannel(channel.getIdLong())) return;

        sendMessage(channel, invoker.getUser(), COLOR_GREEN, "Vote", "You can upvote the bot here: [Click me](https://discordbots.org/bot/565579372128501776)\n\n" +
                "Vote rewards:\n`> +5 breadcrumbs (10 on weekends)`\n`> More rewards are following!`" +
                "\n\n**Note:** Votes might take a while to be registered. Please be patient. The bot will send you a private message after the " +
                "vote was registered but only if your DM's are open.");
    }
}
