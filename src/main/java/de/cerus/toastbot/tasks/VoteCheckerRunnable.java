/*
 * Copyright (c) 2019 Cerus
 * File created at 13.04.19 18:02
 * Last modification: 13.04.19 18:02
 * All rights reserved.
 */

package de.cerus.toastbot.tasks;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import de.cerus.toastbot.commands.user.VoteUCommand;
import de.cerus.toastbot.event.VoteEventCaller;
import de.cerus.toastbot.util.VoteUtil;
import net.dv8tion.jda.api.JDA;
import org.discordbots.api.client.DiscordBotListAPI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VoteCheckerRunnable implements Runnable {
    private JDA jda;
    private DiscordBotListAPI discordBotListAPI;
    private List<Long> voters;
    private CommentedFileConfig voterConfig;
    private VoteEventCaller voteEventCaller;

    private boolean interrupted = false;

    public VoteCheckerRunnable(JDA jda, DiscordBotListAPI discordBotListAPI, VoteEventCaller voteEventCaller) {
        this.jda = jda;
        this.discordBotListAPI = discordBotListAPI;
        this.voteEventCaller = voteEventCaller;

        File file = new File("./Voters.toml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.voterConfig = CommentedFileConfig.of(file);
        this.voterConfig.load();
        if(this.voterConfig.isEmpty()){
            this.voterConfig.set("voters", new ArrayList<Long>());
            this.voterConfig.save();
        }

        this.voters = this.voterConfig.get("voters");

        voters.forEach(l -> VoteUtil.getHasVoted().put(l, true));
    }

    @Override
    public void run() {
        new ArrayList<>(jda.getGuilds()).forEach(guild -> new ArrayList<>(guild.getMembers()).stream().filter(member -> !member.getUser().isBot()).forEach(member -> {
            if(isInterrupted()) return;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
            discordBotListAPI.hasVoted(member.getId()).whenComplete((hasVoted, throwable) -> {
                if(throwable != null){
                } else if(hasVoted && !voters.contains(member.getIdLong())){
                    VoteUtil.getHasVoted().put(member.getIdLong(), true);
                   voters.add(member.getIdLong());
                   voteEventCaller.call(member, guild);
               } else if(!hasVoted && voters.contains(member.getIdLong())){
                    VoteUtil.getHasVoted().put(member.getIdLong(), false);
                   voters.remove(member.getIdLong());
               }
            });
        }));
        if(isInterrupted()) return;
        try {
            Thread.sleep((2*60)*1000);
            run();
        } catch (InterruptedException ignored) {
        }
    }

    public void shutdown(){
        saveVoters();
    }

    public void saveVoters(){
        this.voterConfig.set("voters", voters);
        this.voterConfig.save();
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }
}
