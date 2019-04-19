/*
 * Copyright (c) 2019 Cerus
 * File created at 13.04.19 18:29
 * Last modification: 13.04.19 18:29
 * All rights reserved.
 */

package de.cerus.toastbot.event;

import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class VoteEventCaller {

    private Set<BiConsumer<User, Boolean>> listeners;

    public VoteEventCaller() {
        listeners = new HashSet<>();
    }

    public void registerListener(BiConsumer<User, Boolean> listener) {
        listeners.add(listener);
    }

    public void call(User user, boolean isWeekend) {
        listeners.forEach(listener -> listener.accept(user, isWeekend));
    }
}
