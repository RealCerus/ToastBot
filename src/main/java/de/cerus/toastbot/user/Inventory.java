/*
 * Copyright (c) 2019 Cerus
 * File created at 20.04.19 16:50
 * Last modification: 20.04.19 16:50
 * All rights reserved.
 */

package de.cerus.toastbot.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Inventory {
    public static Inventory ofString(String json) {
        List<Item> items = new ArrayList<>();
        JsonObject object = new JsonParser().parse(json).getAsJsonObject();
        JsonArray array = object.get("items").getAsJsonArray();
        array.forEach(jsonElement -> items.add(Item.ofString(jsonElement.toString())));
        return new Inventory(items);
    }

    private List<Item> items;

    public Inventory(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        if(hasItem(item.getName())){
            Item otherItem = getItem(item.getName());
            items.remove(otherItem);
            if(otherItem == null) return;
            otherItem.setAmount(otherItem.getAmount()+item.getAmount());
            if(otherItem.getAmount() > otherItem.getMaxAmount() && otherItem.getMaxAmount() != -1)
                otherItem.setAmount(otherItem.getMaxAmount());
            items.add(otherItem);
        } else items.add(item);
    }

    public void removeItem(Item item){
        items.remove(item);
    }

    public void removeItem(String name){
        removeItem(name, Integer.MAX_VALUE);
    }

    public Inventory removeItem(String name, int amount){
        if(!hasItem(name)) return this;
        Item item = getItem(name);
        if(item == null) return this;
        items.remove(item);
        item.setAmount(item.getAmount()-amount);
        if(item.getAmount() > 0)
            items.add(item);
        return this;
    }

    public Item getItem(String name) {
        for (Item item : items) {
            if(item.getName().equalsIgnoreCase(name)) return item;
        }
        return null;
    }

    public boolean hasItem(String name) {
        for (Item item : items) {
            if(item.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        if(items.isEmpty())
            return "/";
        return items.stream().map(Item::toString).collect(Collectors.joining(", "));
    }

    public String toJsonString() {
        JsonParser parser = new JsonParser();
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();
        items.forEach(item -> array.add(parser.parse(item.toJsonString()).getAsJsonObject()));
        object.add("items", array);
        return object.toString();
    }
}
