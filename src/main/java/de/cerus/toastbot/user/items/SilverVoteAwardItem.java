/*
 * Copyright (c) 2019 Cerus
 * File created at 21.04.19 01:23
 * Last modification: 21.04.19 01:23
 * All rights reserved.
 */

package de.cerus.toastbot.user.items;

import de.cerus.toastbot.user.Item;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class SilverVoteAwardItem extends Item {
    public SilverVoteAwardItem(int amount) {
        super("Silver vote award", "Received at 25 votes", amount, -1, 250);
        setUsable(false);
    }

    @Override
    public void onUse(User user, TextChannel channel) {
    }
}
