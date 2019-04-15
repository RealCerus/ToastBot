/*
 * Copyright (c) 2019 Cerus
 * File created at 13.04.19 12:56
 * Last modification: 13.04.19 12:56
 * All rights reserved.
 */

package de.cerus.toastbot.economy;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EconomyController {

    private CommentedFileConfig commentedFileConfig;

    public EconomyController(File file) {
        if (file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.commentedFileConfig = CommentedFileConfig.builder(file).autosave().autoreload().build();
        this.commentedFileConfig.load();
    }

    public long getBreadcrumbs(Member member) {
        return getBreadcrumbs(member.getUser());
    }

    public long getBreadcrumbs(User user) {
        return getBreadcrumbs(user.getId());
    }

    public long getBreadcrumbs(String id) {
        return commentedFileConfig.getLongOrElse(id, 0L);
    }

    public void addBreadcrumbs(Member member, long amount) {
        addBreadcrumbs(member.getUser(), amount);
    }

    public void addBreadcrumbs(User user, long amount) {
        addBreadcrumbs(user.getId(), amount);
    }

    public void addBreadcrumbs(String id, long amount) {
        commentedFileConfig.set(id, getBreadcrumbs(id) + amount);
    }

    public void removeBreadcrumbs(Member member, long amount) {
        removeBreadcrumbs(member.getUser(), amount);
    }

    public void removeBreadcrumbs(User user, long amount) {
        removeBreadcrumbs(user.getId(), amount);
    }

    public void removeBreadcrumbs(String id, long amount) {
        commentedFileConfig.set(id, getBreadcrumbs(id) - amount);
    }

    public Map<Long, Long> getTopFive(Guild guild) {
        Map<Long, Long> allUsers = new HashMap<>();
        commentedFileConfig.entrySet().forEach(entry -> {
            System.out.println(entry.getValue().toString() + " " + entry.getValue().getClass().getSimpleName());
            if (entry.getValue() instanceof Number) {
                User user = guild.getJDA().getUserById(entry.getKey());
                if (user != null && guild.getMember(user) != null)
                    allUsers.put(Long.parseLong(entry.getKey()), Long.parseLong(String.valueOf(((Integer) entry.getValue()))));
            }
        });
        System.out.println(allUsers.size());
        Map<Long, Long> sorted = allUsers.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        return sorted;
    }

    public void shutdown() {
        commentedFileConfig.save();
    }
}
