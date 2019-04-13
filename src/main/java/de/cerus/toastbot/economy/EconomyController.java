/*
 * Copyright (c) 2019 Cerus
 * File created at 13.04.19 12:56
 * Last modification: 13.04.19 12:56
 * All rights reserved.
 */

package de.cerus.toastbot.economy;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.IOException;

public class EconomyController {

    private CommentedFileConfig commentedFileConfig;

    public EconomyController(File file) {
        if(file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.commentedFileConfig = CommentedFileConfig.builder(file).autosave().autoreload().build();
    }

    public long getBreadcrumbs(Member member){
        return getBreadcrumbs(member.getUser());
    }

    public long getBreadcrumbs(User user){
        return getBreadcrumbs(user.getId());
    }

    public long getBreadcrumbs(String id){
        return commentedFileConfig.getLongOrElse(id, 0L);
    }

    public void addBreadcrumbs(Member member, long amount){
        addBreadcrumbs(member.getUser(), amount);
    }

    public void addBreadcrumbs(User user, long amount){
        addBreadcrumbs(user.getId(), amount);
    }

    public void addBreadcrumbs(String id, long amount){
        commentedFileConfig.set(id, getBreadcrumbs(id)+amount);
    }

    public void removeBreadcrumbs(Member member, long amount){
        removeBreadcrumbs(member.getUser(), amount);
    }

    public void removeBreadcrumbs(User user, long amount){
        removeBreadcrumbs(user.getId(), amount);
    }

    public void removeBreadcrumbs(String id, long amount){
        commentedFileConfig.set(id, getBreadcrumbs(id)-amount);
    }
}
