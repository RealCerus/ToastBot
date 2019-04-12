/*
 * Copyright (c) 2019 Cerus
 * File created at 11.04.19 23:05
 * Last modification: 11.04.19 23:05
 * All rights reserved.
 */

package de.cerus.toastbot.commands.terminal;

import de.cerus.toastbot.command.TerminalCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.time.format.DateTimeFormatter;

public class GuildsTCommand extends TerminalCommand {
    private JDA jda;

    public GuildsTCommand(JDA jda) {
        super("guilds");
        this.jda = jda;
    }

    @Override
    public void execute(String usedCommand, String[] args) {
        int displayedGuilds = 0;
        for (Guild guild : jda.getGuilds()) {
            if (displayedGuilds > 0)
                System.out.println();
            System.out.println("Guild '" + guild.getName() + "':");
            System.out.println("   Guild id: " + guild.getId());
            System.out.println("   Owner: " + guild.getOwner().getUser().getAsTag());
            System.out.println("   Members: " + guild.getMembers().size());
            System.out.println("   Bot join date: " + guild.getMember(jda.getSelfUser()).getTimeJoined().format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss a")));
            displayedGuilds++;
        }
        if (displayedGuilds > 0)
            System.out.println();
        System.out.println("Total guilds: " + displayedGuilds);
    }
}
