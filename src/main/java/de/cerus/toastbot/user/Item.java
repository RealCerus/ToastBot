/*
 * Copyright (c) 2019 Cerus
 * File created at 20.04.19 16:50
 * Last modification: 20.04.19 16:50
 * All rights reserved.
 */

package de.cerus.toastbot.user;

import net.dv8tion.jda.api.entities.User;

public abstract class Item {

    private String name;
    private String description;
    private int amount;

    public abstract void onUse(User user);
}
