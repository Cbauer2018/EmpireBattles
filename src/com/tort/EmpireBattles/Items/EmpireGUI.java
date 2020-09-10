package com.tort.EmpireBattles.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EmpireGUI {
    public static ItemStack EmpireGUI;

    public static void init(){
        createEmpireGUI();
    }

    private static void createEmpireGUI(){
        ItemStack item = new ItemStack(Material.COMPASS,1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Join an Empire!");
        meta.addEnchant(Enchantment.LUCK,1,false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        EmpireGUI = item;

    }
}
