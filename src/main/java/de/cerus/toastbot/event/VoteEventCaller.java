/*
 * Copyright (c) 2019 Cerus
 * File created at 13.04.19 18:29
 * Last modification: 13.04.19 18:29
 * All rights reserved.
 */

package de.cerus.toastbot.event;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class VoteEventCaller {

    private Set<BiConsumer<Member, Guild>> listeners;

    public VoteEventCaller() {
        listeners = new HashSet<>();
    }

    public void registerListener(BiConsumer<Member, Guild> listener){
        listeners.add(listener);
    }

    public void call(Member member, Guild guild){
        listeners.forEach(listener -> listener.accept(member, guild));
    }
}
