package com.tort.EmpireBattles.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Cosmetics {

    public static ItemStack Cosmetics;

    public static void init(){
        createCaptureTool();
    }

    private static void createCaptureTool(){
        ItemStack item = new ItemStack(Material.BLAZE_POWDER,1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Cosmetics");
        meta.addEnchant(Enchantment.LUCK,1,false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        Cosmetics = item;

    }
}
