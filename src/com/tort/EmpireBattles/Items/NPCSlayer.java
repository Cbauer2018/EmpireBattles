package com.tort.EmpireBattles.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NPCSlayer {

    public static ItemStack NPCSlayer;

    public static void init(){
        createNPCSlayer();
    }

    private static void createNPCSlayer(){
        ItemStack item = new ItemStack(Material.GOLDEN_AXE,1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "NPC Slayer");
        meta.addEnchant(Enchantment.LUCK,1,false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> slayerLore = new ArrayList<>();
        slayerLore.add("");
        slayerLore.add(ChatColor.DARK_GRAY + "I got murder on my mind.");
        meta.setLore(slayerLore);
        item.setItemMeta(meta);

        NPCSlayer = item;

    }
}
