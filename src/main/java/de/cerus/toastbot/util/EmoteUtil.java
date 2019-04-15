/*
 * Copyright (c) 2019 Cerus
 * File created at 13.04.19 23:52
 * Last modification: 13.04.19 23:52
 * All rights reserved.
 */

package de.cerus.toastbot.util;

import net.dv8tion.jda.api.JDA;

public class EmoteUtil {

    private static boolean isInGuild = false;

    public static void initialize(JDA jda){
        isInGuild = jda.getGuilds().stream().filter(guild -> guild.getId().equals("565825337108463626")).iterator().hasNext();
    }

    public static String getBreadcrumbEmote() {
        if(isInGuild)
            return "<:breadcrumb:566584331112415235>";
        else return ":bread:";
    }
}
