/*
 * Copyright (c) 2019 Cerus
 * File created at 20.04.19 16:50
 * Last modification: 20.04.19 16:50
 * All rights reserved.
 */

package de.cerus.toastbot.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.cerus.toastbot.economy.EconomyController;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.lang.reflect.InvocationTargetException;

public abstract class Item {
    public static Item ofString(String json){
        JsonObject object = new JsonParser().parse(json).getAsJsonObject();
        String clazz = object.get("class").getAsString();
        Item item;
        try {
            Class<?> itemClass = Class.forName(clazz);
            Class<? extends Item> castedItemClass = itemClass.asSubclass(Item.class);
            try {
                item = castedItemClass.newInstance();
            } catch (Exception ignored){
                try {
                    item = castedItemClass.getDeclaredConstructor(int.class)
                            .newInstance(object.get("amount").getAsInt());
                } catch (Exception ignored2){
                    item = castedItemClass.getDeclaredConstructor(String.class, String.class, Integer.class, Integer.class, Integer.class)
                            .newInstance(object.get("name").getAsString(), object.get("description").getAsString(), object.get("amount").getAsInt(),
                                    object.get("max-amount").getAsInt(), object.get("worth").getAsInt());
                }
            }
            item.setUsable(object.get("usable").getAsBoolean());
            return item;
        } catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String name;
    private String description;
    private int amount;
    private int maxAmount;
    private int worth;

    private boolean canBeUsed = true;

    public Item(String name, String description, Integer amount, Integer maxAmount, Integer worth) {
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.maxAmount = maxAmount;
        this.worth = worth;
    }

    public abstract void onUse(User user, TextChannel channel);

    public void sell(EconomyController economyController, User user){
        economyController.addBreadcrumbs(user, getWorth());
        ToastBotUser toastBotUser = new ToastBotUser(user);
        toastBotUser.getInventory().removeItem(getName(), 1);
        toastBotUser.save();
    }

    @Override
    public String toString() {
        return amount+"x "+name+" (worth: "+(worth*amount)+")";
    }

    public String toJsonString(){
        JsonObject object = new JsonObject();
        object.addProperty("class", getClass().getName());
        object.addProperty("name", name);
        object.addProperty("description", description);
        object.addProperty("amount", amount);
        object.addProperty("max-amount", maxAmount);
        object.addProperty("worth", worth);
        object.addProperty("usable", canBeUsed);
        return object.toString();
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getWorth() {
        return worth;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isUsable() {
        return canBeUsed;
    }

    public void setUsable(boolean canBeUsed) {
        this.canBeUsed = canBeUsed;
    }
}
