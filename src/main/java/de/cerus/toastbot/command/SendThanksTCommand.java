/*
 * Copyright (c) 2019 Cerus
 * File created at 15.04.19 12:59
 * Last modification: 15.04.19 12:59
 * All rights reserved.
 */

package de.cerus.toastbot.command;

import de.cerus.toastbot.util.VoteUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

public class SendThanksTCommand extends TerminalCommand {
    private JDA jda;

    public SendThanksTCommand(JDA jda) {
        super("sendthanks");
        this.jda = jda;
    }

    @Override
    public void execute(String usedCommand, String[] args) {
        if(args.length == 0){
            System.out.println("Usage: sendthanks <user id>");
            return;
        }
        User user;
        try {
            user = jda.getUserById(args[0]);
        } catch (Exception ignored){
            System.out.println("User not found");
            return;
        }

        try {
            PrivateChannel channel = user.openPrivateChannel().complete();
            channel.sendMessage("// Test Message //\nby "+jda.getUserById(347018538713874444L).getAsMention()).complete();
            channel.sendMessage(VoteUtil.getThankYouMessage(user, false)).complete();
            System.out.println("Success");
        } catch (Exception e){
            System.out.println("Failed: "+e.getMessage());
        }
    }
}
