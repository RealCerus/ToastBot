/*
 * Copyright (c) 2019 Cerus
 * File created at 21.04.19 00:43
 * Last modification: 21.04.19 00:43
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.user.Item;
import de.cerus.toastbot.user.items.GlobalItemRegistry;
import de.cerus.toastbot.util.BotChannelUtil;
import de.cerus.toastbot.util.EmoteUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.stream.Collectors;

public class ItemInfoUCommand extends UserCommand {
    public ItemInfoUCommand() {
        super("item-info");
        setUsage(getCommand() + " [item]");
        setDescription("Sends information about the specified item.");
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if (!BotChannelUtil.isBotChannel(channel)) {
            sendNoCommandChannelFailure(channel, invoker.getUser());
            return;
        }

        StringBuilder itemName = new StringBuilder();
        for (String arg : args) {
            itemName.append(" ").append(arg);
        }

        Item item = GlobalItemRegistry.getItem(itemName.toString().trim());
        if (item == null) {
            channel.sendMessage(
                    new EmbedBuilder()
                            .setColor(COLOR_GREEN)
                            .addField("All registered items:", GlobalItemRegistry.getItems().size() + " items | " +
                                    GlobalItemRegistry.getItems().stream().map(Item::toString).collect(Collectors.joining(", ")), false)
                            .build()
            ).complete();
            return;
        }

        channel.sendMessage(
                new EmbedBuilder()
                        .setTitle("Item information")
                        .setColor(COLOR_GREEN)
                        .addField("Name:", item.getName(), false)
                        .addField("Description:", item.getDescription(), false)
                        .addField("Maximum amount:", item.getMaxAmount() == -1 ? "Unlimited" : String.valueOf(item.getMaxAmount()), false)
                        .addField("Worth:", item.getWorth() + " " + EmoteUtil.getBreadcrumbEmote(), false)
                        .build()
        ).complete();
    }
}
