/*
 * Copyright (c) 2019 Cerus
 * File created at 19.04.19 20:47
 * Last modification: 19.04.19 20:46
 * All rights reserved.
 */

package de.cerus.toastbot.util;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TopVoterUtil {

    private static Map<Long, Long> votes = new HashMap<>();
    private static CommentedFileConfig voters;
    private static JDA jda;

    public static void initialize(JDA jda) {
        TopVoterUtil.jda = jda;

        File file = new File("./Votes.toml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        voters = CommentedFileConfig.of(file);
        voters.load();

        voters.entrySet().forEach(entry -> {
            try {
                Long userId = Long.parseLong(entry.getKey());
                Long votes = Long.parseLong(String.valueOf((int) entry.getValue()));
                TopVoterUtil.votes.put(userId, votes);
            } catch (Exception ignored) {
            }
        });
    }

    public static void addVote(User user) {
        votes.put(user.getIdLong(), votes.getOrDefault(user.getIdLong(), 0L) + 1L);
        voters.set(user.getId(), votes.get(user.getIdLong()));
        voters.save();
    }

    public static long getVotes(User user){
        return votes.getOrDefault(user.getIdLong(), 0L);
    }

    public static Map<Long, Long> getTopFive() {
        Map<Long, Long> allUsers = new HashMap<>();
        votes.forEach((key, value) -> {
            User user = jda.getUserById(key);
            if (user != null)
                allUsers.put(user.getIdLong(), value);
        });
        return allUsers.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
