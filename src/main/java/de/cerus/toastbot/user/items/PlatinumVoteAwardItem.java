/*
 * Copyright (c) 2019 Cerus
 * File created at 21.04.19 01:24
 * Last modification: 21.04.19 01:24
 * All rights reserved.
 */

package de.cerus.toastbot.user.items;

import de.cerus.toastbot.user.Item;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class PlatinumVoteAwardItem extends Item {
    public PlatinumVoteAwardItem(int amount) {
        super("Platinum vote award", "Received at 100 votes", amount, -1, 1000);
        setUsable(false);
    }

    @Override
    public void onUse(User user, TextChannel channel) {
    }
}
