/*
 * Copyright (c) 2019 Cerus
 * File created at 21.04.19 01:16
 * Last modification: 21.04.19 01:16
 * All rights reserved.
 */

package de.cerus.toastbot.user.items;

import de.cerus.toastbot.user.Item;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class BronzeVoteAwardItem extends Item {
    public BronzeVoteAwardItem(int amount) {
        super("Bronze vote award", "Received at 10 votes", amount, -1, 100);
        setUsable(false);
    }

    @Override
    public void onUse(User user, TextChannel channel) {
    }
}
