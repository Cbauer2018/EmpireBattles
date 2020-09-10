package com.tort.EmpireBattles.GUI;

import com.tort.EmpireBattles.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class GuiManager {
    public static Inventory JoinEmpireGUI;
    public static Inventory SmithingTable;








    public void createInventory(){
        Inventory inv = Bukkit.createInventory(null,9, ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] ");


        ItemStack yellow = new ItemStack(Material.YELLOW_WOOL);
        ItemMeta yellowMeta = yellow.getItemMeta();
        yellowMeta.setDisplayName(ChatColor.YELLOW + "OTTOMANS");
        List<String> yellowLore = new ArrayList<>();
        yellowLore.add("");
        yellowLore.add(ChatColor.GRAY + "Click here to join the" + ChatColor.YELLOW + " OTTOMANS");
        yellowMeta.setLore(yellowLore);
        yellow.setItemMeta(yellowMeta);
        inv.setItem(2,yellow);

        ItemStack blue = new ItemStack(Material.BLUE_WOOL);
        ItemMeta blueMeta = blue.getItemMeta();
        blueMeta.setDisplayName(ChatColor.DARK_BLUE + "MONGOLS");
        List<String> blueLore = new ArrayList<>();
        blueLore.add("");
        blueLore.add(ChatColor.GRAY + "Click here to join the" + ChatColor.DARK_BLUE+ " MONGOLS");
        blueMeta.setLore(blueLore);
        blue.setItemMeta(blueMeta);
        inv.setItem(3,blue);

        ItemStack red = new ItemStack(Material.RED_WOOL);
        ItemMeta redMeta = red.getItemMeta();
        redMeta.setDisplayName(ChatColor.DARK_RED+ "ROMANS");
        List<String> redLore = new ArrayList<>();
        redLore.add("");
        redLore.add(ChatColor.GRAY + "Click here to join the" + ChatColor.DARK_RED + " ROMANS");
        redMeta.setLore(redLore);
        red.setItemMeta(redMeta);
        inv.setItem(4,red);

        ItemStack purple = new ItemStack(Material.PURPLE_WOOL);
        ItemMeta purpleMeta = purple.getItemMeta();
        purpleMeta.setDisplayName(ChatColor.DARK_PURPLE + "VIKINGS");
        List<String> purpleLore = new ArrayList<>();
        purpleLore.add("");
        purpleLore.add(ChatColor.GRAY + "Click here to join the" + ChatColor.DARK_PURPLE + " VIKINGS");
        purpleMeta.setLore(purpleLore);
        purple.setItemMeta(purpleMeta);
        inv.setItem(5,purple);

        JoinEmpireGUI = inv;

    }

    public static Inventory getJoinEmpireGUI() {
        return JoinEmpireGUI;
    }

    public Inventory townSpawnGUI(String empire){
        Inventory inv = Bukkit.createInventory(null,36,  "Towns");
        // Use in futue for title ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "]" + ChatColor.GOLD + " Towns"
        ArrayList<String> towns = new ArrayList<>();
        for(Map.Entry<String, String > entry: Main.CaptureOwners.entrySet()){
            if(Objects.equals(empire.toUpperCase(), entry.getValue())){
                towns.add(entry.getKey());
            }
        }

        if(towns.isEmpty()){
            ItemStack barrier = new ItemStack(Material.BARRIER);
            ItemMeta barrierMeta = barrier.getItemMeta();
            barrierMeta.setDisplayName(ChatColor.DARK_RED + "Your Empire has no towns! Go Capture!");
            List<String> townLore = new ArrayList<>();
            townLore.add("");
            barrierMeta.setLore(townLore);
            barrier.setItemMeta(barrierMeta);
            inv.setItem(0 ,barrier);
            return inv;
        }
        int ironTowns = 0;
        int goldTowns = 0;
        int diamondTowns = 0;
        int netheriteTowns = 0;

        for(int i = 0; i< towns.size();i++){
            ItemStack townIcon;
            if(Main.TownType.get(towns.get(i)).equals("iron")){
                townIcon = new ItemStack(Material.IRON_INGOT);
                ironTowns++;
            }else if(Main.TownType.get(towns.get(i)).equals("gold")){
                townIcon = new ItemStack(Material.GOLD_INGOT);
                goldTowns++;
            }else if(Main.TownType.get(towns.get(i)).equals("diamond")){
                townIcon = new ItemStack(Material.DIAMOND);
                diamondTowns++;
            }else{
                townIcon = new ItemStack(Material.NETHERITE_INGOT);
                netheriteTowns++;
            }
            ItemMeta townMeta = townIcon.getItemMeta();
            townMeta.setDisplayName(ChatColor.DARK_GRAY + towns.get(i));
            List<String> townLore = new ArrayList<>();
            townLore.add("");
            townLore.add(ChatColor.GREEN + "Teleport to " + towns.get(i));
            townLore.add(ChatColor.GRAY + Main.TownType.get(towns.get(i)) + " town");
            townMeta.setLore(townLore);
            townIcon.setItemMeta(townMeta);

            if(Main.TownType.get(towns.get(i)).equals("iron")){
                inv.setItem(27 + ironTowns - 1  , townIcon);
            }else if(Main.TownType.get(towns.get(i)).equals("gold")){
                inv.setItem(18 + goldTowns - 1, townIcon);
            }else if(Main.TownType.get(towns.get(i)).equals("diamond")){
                inv.setItem(9 + diamondTowns - 1, townIcon);
            }else{
                inv.setItem(0 + netheriteTowns - 1 , townIcon);
            }
        }




        return  inv;
    }
}
