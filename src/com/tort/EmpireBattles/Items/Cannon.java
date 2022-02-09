package com.tort.EmpireBattles.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Cannon {
    public static ItemStack cannon;

    public static void init(){
        createCannon();
    }

    private static void createCannon(){
        ItemStack item = new ItemStack(Material.OBSERVER,1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Cannon");
        item.setItemMeta(meta);

        cannon = item;

    }
}
