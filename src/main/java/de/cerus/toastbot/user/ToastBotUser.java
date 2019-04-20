/*
 * Copyright (c) 2019 Cerus
 * File created at 20.04.19 16:49
 * Last modification: 20.04.19 16:49
 * All rights reserved.
 */

package de.cerus.toastbot.user;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import de.cerus.toastbot.economy.EconomyController;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ToastBotUser {

    private User user;
    private Inventory inventory;
    private int toastBattleHP;

    private CommentedFileConfig commentedFileConfig;

    public ToastBotUser(User user, Inventory inventory, int toastBattleHP) {
        this.user = user;
        this.inventory = inventory;
        this.toastBattleHP = toastBattleHP;

        File file = new File("./user-data/" + user.getId() + ".toml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        commentedFileConfig = CommentedFileConfig.of(file);
        commentedFileConfig.load();

        if (commentedFileConfig.contains("hp"))
            this.toastBattleHP = commentedFileConfig.get("hp");

        if (commentedFileConfig.contains("inventory"))
            this.inventory = Inventory.ofString(commentedFileConfig.get("inventory"));
    }

    public ToastBotUser(User user, int toastBattleHP) {
        this(user, new Inventory(new ArrayList<>()), toastBattleHP);
    }

    public ToastBotUser(User user) {
        this(user, new Inventory(new ArrayList<>()), 100);
    }

    public int getNextUpgradeHP() {
        return toastBattleHP >= 150 ? -1 : toastBattleHP + 10;
    }

    public int getNextUpgradeCost() {
        return toastBattleHP >= 150 ? -1 : getNextUpgradeCost0();
    }

    private int getNextUpgradeCost0() {
        int nextHp = getNextUpgradeHP();
        switch (nextHp) {
            default:
            case 110:
                return 50;
            case 120:
                return 75;
            case 130:
                return 100;
            case 140:
                return 150;
            case 150:
                return 200;
        }
    }

    public boolean canUpgrade() {
        return getNextUpgradeHP() != -1;
    }

    public boolean canUpgrade(EconomyController economyController) {
        return economyController.getBreadcrumbs(user) >= getNextUpgradeCost() && canUpgrade();
    }

    public boolean upgrade(EconomyController economyController) {
        int nextHp = getNextUpgradeHP();
        if (nextHp == -1) return false;
        int cost = getNextUpgradeCost();
        if (economyController.getBreadcrumbs(user) < cost) return false;
        economyController.removeBreadcrumbs(user, cost);
        toastBattleHP = nextHp;
        save();
        return true;
    }

    public boolean upgradeFree() {
        int nextHp = getNextUpgradeHP();
        if (nextHp == -1) return false;
        toastBattleHP = nextHp;
        save();
        return true;
    }

    public void save() {
        commentedFileConfig.set("hp", toastBattleHP);
        commentedFileConfig.set("inventory", inventory.toJsonString());
        commentedFileConfig.save();
    }

    public User getUser() {
        return user;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getToastBattleHP() {
        return toastBattleHP;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setToastBattleHP(int toastBattleHP) {
        this.toastBattleHP = toastBattleHP;
    }
}
