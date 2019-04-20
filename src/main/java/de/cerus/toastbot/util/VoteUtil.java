/*
 * Copyright (c) 2019 Cerus
 * File created at 14.04.19 11:51
 * Last modification: 14.04.19 11:51
 * All rights reserved.
 */

package de.cerus.toastbot.util;

import at.mukprojects.giphy4j.Giphy;
import at.mukprojects.giphy4j.exception.GiphyException;
import de.cerus.toastbot.economy.EconomyController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class VoteUtil {

    private static EconomyController economyController;
    private static Giphy giphy;

    private static Map<Long, Boolean> hasVoted = new HashMap<>();

    public static void initialize(EconomyController economyController, Giphy giphy){
        VoteUtil.economyController = economyController;
        VoteUtil.giphy = giphy;
    }

    public static Map<Long, Boolean> getHasVoted() {
        return hasVoted;
    }

    public static void setHasVoted(Map<Long, Boolean> hasVoted) {
        VoteUtil.hasVoted = hasVoted;
    }

    public static MessageEmbed getThankYouMessage(User member, Boolean isWeekend, int multiplier) {
        return new EmbedBuilder()
                .setTitle("Thanks for your vote")
                .setColor(Color.magenta)
                .setDescription("Thanks for voting for this bot at [DiscordBots.org](https://discordbots.org)! " +
                        "You received "+(isWeekend ? "**20 (weekend bonus)**" : "10")+" " + EmoteUtil.getBreadcrumbEmote() +
                        (multiplier == 0 ? "" : " (+"+multiplier+" bonus breadcrumbs)") +". Total " + EmoteUtil.getBreadcrumbEmote()
                        + ": " + economyController.getBreadcrumbs(member))
                .setImage(randomThankYouGif())
                .build();
    }

    private static String randomThankYouGif() {
        try {
            return giphy.searchRandom("thank you").getData().getImageUrl();
        } catch (GiphyException e) {
            e.printStackTrace();
            return "http://some.url";
        }
    }
}
