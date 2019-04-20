/*
 * Copyright (c) 2019 Cerus
 * File created at 20.04.19 17:04
 * Last modification: 20.04.19 17:04
 * All rights reserved.
 */

package de.cerus.toastbot.commands.user;

import de.cerus.toastbot.command.UserCommand;
import de.cerus.toastbot.economy.EconomyController;
import de.cerus.toastbot.user.ToastBotUser;
import de.cerus.toastbot.util.BotChannelUtil;
import de.cerus.toastbot.util.EmoteUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class UpgradeHpUCommand extends UserCommand {
    private EconomyController economyController;

    public UpgradeHpUCommand(EconomyController economyController) {
        super("upgrade-hp");
        this.economyController = economyController;
        setDescription("Upgrade your hp for toastbattles.");
    }

    @Override
    public void execute(String usedCommand, Member invoker, Message message, TextChannel channel, String[] args) {
        if (!BotChannelUtil.isBotChannel(channel)) {
            sendNoCommandChannelFailure(channel, invoker.getUser());
            return;
        }

        ToastBotUser user = new ToastBotUser(invoker.getUser());
        boolean canUpgrade = user.canUpgrade();
        if (!canUpgrade) {
            sendFailure(channel, invoker.getUser(), "Seems like you reached the maximum hp.");
            return;
        }

        canUpgrade = user.canUpgrade(economyController);
        if (!canUpgrade) {
            sendFailure(channel, invoker.getUser(), "Seems like you don't have enough " +
                    EmoteUtil.getBreadcrumbEmote() + " (" + user.getNextUpgradeCost() + " " + EmoteUtil.getBreadcrumbEmote() + " are needed).");
            return;
        }

        boolean upgradeSuccessful = user.upgrade(economyController);
        if(!upgradeSuccessful){
            sendFailure(channel, invoker.getUser(), "Failed to upgrade");
            return;
        }

        sendSuccess(channel, invoker.getUser(), "Upgrade successful! You now have "+user.getToastBattleHP()+" HP.");
    }
}
