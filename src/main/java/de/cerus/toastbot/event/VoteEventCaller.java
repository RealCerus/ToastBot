/*
 * Copyright (c) 2019 Cerus
 * File created at 13.04.19 18:29
 * Last modification: 13.04.19 18:29
 * All rights reserved.
 */

package de.cerus.toastbot.event;

import de.cerus.toastbot.util.TriConsumer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.HashSet;
import java.util.Set;

public class VoteEventCaller {

    private Set<TriConsumer<Member, Guild, Boolean>> listeners;

    public VoteEventCaller() {
        listeners = new HashSet<>();
    }

    public void registerListener(TriConsumer<Member, Guild, Boolean> listener) {
        listeners.add(listener);
    }

    public void call(Member member, Guild guild, boolean isWeekend) {
        listeners.forEach(listener -> listener.accept(member, guild, isWeekend));
    }
}
