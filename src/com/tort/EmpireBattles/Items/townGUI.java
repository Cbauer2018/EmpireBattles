package com.tort.EmpireBattles.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class townGUI {
    public static ItemStack townGUI;

    public static void init(){
        createTownGUI();
    }

    private static void createTownGUI(){
        ItemStack item = new ItemStack(Material.COMPASS,1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Town Teleport");

        item.setItemMeta(meta);

        townGUI = item;

    }
}
