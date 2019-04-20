/*
 * Copyright (c) 2019 Cerus
 * File created at 20.04.19 16:50
 * Last modification: 20.04.19 16:50
 * All rights reserved.
 */

package de.cerus.toastbot.user;

import java.util.Set;

public class Inventory {

    private Set<Item> items;

    public Inventory(Set<Item> items) {
        this.items = items;
    }

    public Set<Item> getItems() {
        return items;
    }
}
