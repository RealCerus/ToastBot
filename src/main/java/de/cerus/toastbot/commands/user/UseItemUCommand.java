/*
 * Copyright (c) 2019 Cerus
 * File created at 21.04.19 01:03
 * Last modification: 21.04.19 01:03
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.user.Item;
import de.cerus.toastbot.user.ToastBotUser;
import de.cerus.toastbot.util.BotChannelUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class UseItemUCommand extends UserCommand {
    public UseItemUCommand() {
        super("use-item");
        setUsage(getCommand() + " <item>");
        setDescription("Use the specified item.");
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if (!BotChannelUtil.isBotChannel(channel)) {
            sendNoCommandChannelFailure(channel, invoker.getUser());
            return;
        }

        if (args.length == 0) {
            sendMessage(channel, invoker.getUser(), COLOR_GREEN, "Command '" + usedCommand + "'",
                    "Correct usage:\n`" + getSettings().getCommandPrefix(channel.getGuild()) + "use-item <item>`");
            return;
        }

        StringBuilder itemNameBuilder = new StringBuilder();
        for (String arg : args) {
            itemNameBuilder.append(" ").append(arg);
        }
        String itemName = itemNameBuilder.toString().trim();

        ToastBotUser user = new ToastBotUser(invoker.getUser());
        if(!user.getInventory().hasItem(itemName)){
            sendFailure(channel, invoker.getUser(), "You do not have this item.");
            return;
        }

        Item item = user.getInventory().getItem(itemName);
        if(item == null){
            sendFailure(channel, invoker.getUser(), "Item is null");
            return;
        }
        if(!item.isUsable()){
            sendFailure(channel, invoker.getUser(), "This item cant be used.");
            return;
        }

        item.onUse(invoker.getUser(), channel);
        user.getInventory().removeItem(item.getName(), 1);
        user.save();
        message.addReaction("\u2705").complete();
    }
}
