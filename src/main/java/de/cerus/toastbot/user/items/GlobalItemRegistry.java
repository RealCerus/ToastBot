/*
 * Copyright (c) 2019 Cerus
 * File created at 21.04.19 00:36
 * Last modification: 21.04.19 00:36
 * All rights reserved.
 */

package de.cerus.toastbot.user.items;

import de.cerus.toastbot.user.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlobalItemRegistry {

    private static List<Item> items = new ArrayList<>();

    public static void registerItem(Item item){
        items.add(item);
    }

    public static void registerItems(Item... items){
        GlobalItemRegistry.items.addAll(Arrays.asList(items));
    }

    public static boolean itemExists(String name){
        for (Item item : items) {
            if(item.getName().equals(name)) return true;
        }
        return false;
    }

    public static boolean itemExists(Class<? extends Item> clazz){
        for (Item item : items) {
            if(item.getClass().getName().equals(clazz.getName())) return true;
        }
        return false;
    }

    public static Item getItem(String name){
        for (Item item : items) {
            if(item.getName().equalsIgnoreCase(name)) return item;
        }
        return null;
    }

    public static List<Item> getItems() {
        return items;
    }
}
