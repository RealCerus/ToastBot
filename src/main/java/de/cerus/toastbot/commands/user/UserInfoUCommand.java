/*
 * Copyright (c) 2019 Cerus
 * File created at 20.04.19 17:13
 * Last modification: 20.04.19 17:13
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.economy.EconomyController;
import de.cerus.toastbot.user.ToastBotUser;
import de.cerus.toastbot.util.BotChannelUtil;
import de.cerus.toastbot.util.EmoteUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class UserInfoUCommand extends UserCommand {
    private EconomyController economyController;

    public UserInfoUCommand(EconomyController economyController) {
        super("user-info");
        this.economyController = economyController;
        setUsage(getCommand() + " [@<user>]");
        setDescription("Shows information about a user or yourself.");
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if (!BotChannelUtil.isBotChannel(channel)) {
            sendNoCommandChannelFailure(channel, invoker.getUser());
            return;
        }

        Member member = message.getMentionedMembers().size() == 1 ? message.getMentionedMembers().get(0) : invoker;
        ToastBotUser user = new ToastBotUser(member.getUser());

        channel.sendMessage(
                new EmbedBuilder()
                        .setColor(COLOR_GREEN)
                        .setTitle(member.getUser().getAsTag())
                        .setDescription("Information about " + member.getUser().getAsTag() + ":")
                        .setThumbnail(member.getUser().getAvatarUrl() == null ? member.getUser().getDefaultAvatarUrl() : member.getUser().getAvatarUrl())
                        .addField("ID:", member.getId(), false)
                        .addField("Toastbattle HP:", user.getToastBattleHP() + " / 150 HP", false)
                        .addField("Next upgrade:", user.getNextUpgradeHP() == -1 ? "/" : user.getNextUpgradeHP() + " / 150 HP", false)
                        .addField("Next upgrade cost:", user.getNextUpgradeCost() == -1 ? "/" : user.getNextUpgradeCost() + " " + EmoteUtil.getBreadcrumbEmote(), false)
                        .addField("Can upgrade:", user.canUpgrade(economyController) ? ":white_check_mark:" : ":no_entry_sign:", false)
                        .addField("Breadcrumbs:", economyController.getBreadcrumbs(member)+" "+EmoteUtil.getBreadcrumbEmote(), false)
                        .addField("Inventory:", user.getInventory().toString(), false)
                        .setFooter(invoker.getUser().getAsTag(), invoker.getUser().getAvatarUrl())
                        .build()
        ).complete();
    }
}
