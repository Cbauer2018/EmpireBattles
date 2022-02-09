package com.tort.EmpireBattles.GUI;

import com.tort.EmpireBattles.Empires.Empire;
import com.tort.EmpireBattles.Files.Colors;
import com.tort.EmpireBattles.Files.EmpireUtils;

import com.tort.EmpireBattles.Items.Cannon;
import com.tort.EmpireBattles.Main;

import com.tort.EmpireBattles.Towns.Town;
import net.minecraft.server.v1_16_R2.Potions;
import org.bukkit.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class GuiManager {
    public static Inventory JoinEmpireGUI;
    public static Inventory storeInventory;
    public static Inventory CosmeticGUI;
    public static Inventory arrowTrailGUI;
    public static Inventory particleGUI;


    public Main plugin;


    public GuiManager(Main plugin){
        this.plugin = plugin;

    }






    public void createInventory(){
        Inventory inv = Bukkit.createInventory(null,9, ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "MC" + ChatColor.GOLD + "E" + ChatColor.DARK_GRAY + "] ");

        ArrayList<Empire> empires = plugin.empireManager.getActiveEmpires();

        for(int i = 0; i < empires.size(); i++){
            ChatColor color = empires.get(i).getEmpireColor();
            ItemStack empireItem = new ItemStack(Colors.translateChatColorToWool(color));
            ItemMeta empireMeta = empireItem.getItemMeta();
            empireMeta.setDisplayName(color + empires.get(i).getName());
            empireMeta.setLocalizedName(empires.get(i).getREFERENCE());
            List<String> empireLore = new ArrayList<>();
            empireLore.add("");
            empireLore.add(ChatColor.GRAY + "Click here to join " + color + empires.get(i).getName());
            empireLore.add(ChatColor.GREEN + "Players online: " + empires.get(i).getEmpirePlayerList().size());
            empireMeta.setLore(empireLore);
            empireItem.setItemMeta(empireMeta);

            inv.setItem(i,empireItem);

        }
        JoinEmpireGUI = inv;

    }

    public void createStoreGUI(){
        Inventory inv = Bukkit.createInventory(null,36,  ChatColor.GREEN + "" + ChatColor.BOLD + "Store");

        if(plugin.getConfig().getConfigurationSection("store") != null) {
            Collection<String> storeItems = plugin.getConfig().getConfigurationSection("store").getKeys(false);

            int slot = 1;
            for(String item: storeItems){

                if(Objects.equals(item, "CANNON")) {

                    ItemStack cannonStore = new ItemStack(Material.OBSERVER);
                    ItemMeta cannonMeta = cannonStore.getItemMeta();
                    cannonMeta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + EmpireUtils.firstLetterCap(item));

                    int price = plugin.getConfig().getInt("store." + item + ".price");

                    cannonMeta.setLocalizedName(String.valueOf(price));
                    List<String> itemLore = new ArrayList<>();
                    itemLore.add(ChatColor.DARK_GREEN + "Buy for " +  ChatColor.GOLD + "" + ChatColor.BOLD + price + "$ gold");
                    cannonMeta.setLore(itemLore);
                    cannonStore.setItemMeta(cannonMeta);
                    inv.setItem(slot,cannonStore);



                }else if(item.contains("_ARROW")){  //Need Special parsing. Material Enum does not contain tipped arrows and potions
                    int amount = plugin.getConfig().getInt("store." + item + ".amount");

                    String type = plugin.getConfig().getString("store." + item + ".type");

                    ItemStack storeItem = new ItemStack(Material.TIPPED_ARROW, amount);
                    PotionMeta itemMeta = (PotionMeta) storeItem.getItemMeta();
                    itemMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + EmpireUtils.firstLetterCap(item));
                    itemMeta.setBasePotionData(new PotionData(PotionType.valueOf(type)));

                    int price = plugin.getConfig().getInt("store." + item + ".price");
                    itemMeta.setLocalizedName(String.valueOf(price)); // Will grab price from localized name instead of from the Item lore. Easier and won't have to strip color
                    itemMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + EmpireUtils.firstLetterCap(item.replace("_"," ")));

                    List<String> itemLore = new ArrayList<>();
                    itemLore.add(ChatColor.DARK_GREEN + "Buy for " +  ChatColor.GOLD + "" + ChatColor.BOLD + price + "$ gold");
                    itemMeta.setLore(itemLore);
                    storeItem.setItemMeta(itemMeta);
                    inv.setItem(slot,storeItem);


                }else if(item.contains("_POTION")) {
                    String type = plugin.getConfig().getString("store." + item + ".type");
                    int duration = plugin.getConfig().getInt("store." + item + ".duration");
                    int amplifier = plugin.getConfig().getInt("store." + item + ".amplifier");
                    int amount = plugin.getConfig().getInt("store." + item + ".amount");
                    
                    ItemStack potion = new ItemStack(Material.POTION, amount);
                    PotionMeta meta = (PotionMeta) potion.getItemMeta();
                    meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + EmpireUtils.firstLetterCap(item.replace("_"," ")));
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(type), duration, amplifier), false);

                    int price = plugin.getConfig().getInt("store." + item + ".price");
                    meta.setLocalizedName(String.valueOf(price)); // Will grab price from localized name instead of from the Item lore. Easier and won't have to strip color


                    List<String> itemLore = new ArrayList<>();
                    itemLore.add(ChatColor.DARK_GREEN + "Buy for " +  ChatColor.GOLD + "" + ChatColor.BOLD + price + "$ gold");
                    meta.setLore(itemLore);
                    potion.setItemMeta(meta);
                    inv.setItem(slot,potion);


                }else{
                    Material material = Material.valueOf(item.toUpperCase());

                    int amount = plugin.getConfig().getInt("store." + item + ".amount");
                    ItemStack storeItem = new ItemStack(material, amount);
                    ItemMeta itemMeta = storeItem.getItemMeta();
                    itemMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + EmpireUtils.firstLetterCap(item.replace("_"," ")));

                    int price = plugin.getConfig().getInt("store." + item + ".price");
                    itemMeta.setLocalizedName(String.valueOf(price)); // Will grab price from localized name instead of from the Item lore. Easier and won't have to strip color
                    List<String> itemLore = new ArrayList<>();
                    itemLore.add(ChatColor.DARK_GREEN + "Buy for " +  ChatColor.GOLD + "" + ChatColor.BOLD + price + "$ gold");
                    itemMeta.setLore(itemLore);
                    storeItem.setItemMeta(itemMeta);
                    inv.setItem(slot,storeItem);

                }

                if(Objects.equals(slot % 8,0)){
                    slot += 3;
                }else {
                    slot += 2;
                }
            }
        }

        storeInventory = inv;
    }


    public  Inventory getJoinEmpireGUI() {
        return JoinEmpireGUI;
    }


    public  Inventory getStoreInventory() {
        return storeInventory;
    }

    public Inventory townSpawnGUI(String empire , Location playerLocation){
        Inventory inv = Bukkit.createInventory(null,45,  "Towns");
        // Use in futue for title ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "]" + ChatColor.GOLD + " Towns"
        ArrayList<Town> towns = new ArrayList<>();
        for(Town town : plugin.townManager.getActiveTowns()){
            towns.add(town);
        }

        Empire e = plugin.empireManager.getEmpire(empire);
        ItemStack empireIcon = new ItemStack(Material.BEACON);
        ItemMeta empireMeta = empireIcon.getItemMeta();
        empireMeta.setDisplayName(e.getEmpireColor() + e.getName() + "'s capital");
        empireMeta.setLocalizedName(e.getREFERENCE());
        List<String> empireLore = new ArrayList<>();
        empireLore.add(ChatColor.GREEN + "Teleport to your capital!");
        empireMeta.setLore(empireLore);
        empireIcon.setItemMeta(empireMeta);


        int ironTowns = 0;
        int goldTowns = 0;
        int diamondTowns = 0;
        int netheriteTowns = 0;

        int playerX = (int) playerLocation.getX();
        int playerY = (int) playerLocation.getY();
        int playerZ = (int) playerLocation.getZ();

        for(int i = 0; i< towns.size();i++){
            ItemStack townIcon;
            if(towns.get(i).getType().equals("IRON")){
                townIcon = new ItemStack(Material.IRON_INGOT);
                ironTowns++;
            }else if(towns.get(i).getType().equals("GOLD")){
                townIcon = new ItemStack(Material.GOLD_INGOT);
                goldTowns++;
            }else if(towns.get(i).getType().equals("DIAMOND")){
                townIcon = new ItemStack(Material.DIAMOND);
                diamondTowns++;
            }else{
                townIcon = new ItemStack(Material.NETHERITE_INGOT);
                netheriteTowns++;
            }
            ChatColor chatColor;
            if(Objects.equals(towns.get(i).getOwner(),"NEUTRAL")){
                chatColor = ChatColor.WHITE;
            }else{
                chatColor = plugin.empireManager.getEmpire(towns.get(i).getOwner()).getEmpireColor();
            }
            ItemMeta townMeta = townIcon.getItemMeta();
            townMeta.setDisplayName( chatColor + towns.get(i).getName());
            townMeta.setLocalizedName(towns.get(i).getREFERENCE());
            List<String> townLore = new ArrayList<>();
            townLore.add("");



            int capX = (int) towns.get(i).getTownCaptureZone().getX();
            int capY = (int) towns.get(i).getTownCaptureZone().getY();
            int capZ = (int) towns.get(i).getTownCaptureZone().getZ();

            int d = (int) Math.sqrt(Math.pow(capX-playerX,2) + Math.pow(capY - playerY,2) + Math.pow(capZ-playerZ,2));

            townLore.add(ChatColor.GOLD + "" + d + " block away");

            if(Objects.equals(towns.get(i).getOwner(),e.getREFERENCE())){
                townLore.add(ChatColor.GREEN + "Teleport to " + towns.get(i).getName());
            }
            townMeta.setLore(townLore);
            townIcon.setItemMeta(townMeta);

            if(towns.get(i).getType().equals("IRON")){
                inv.setItem(27 + ironTowns - 1  , townIcon);
            }else if(towns.get(i).getType().equals("GOLD")){
                inv.setItem(18 + goldTowns - 1, townIcon);
            }else if(towns.get(i).getType().equals("DIAMOND")){
                inv.setItem(9 + diamondTowns - 1, townIcon);
            }else{
                inv.setItem(0 + netheriteTowns - 1 , townIcon);
            }

        }


        inv.setItem(40,empireIcon);

        //Add Enemy Empires to Inventory. When clicked compass with point to the empire's capital.
        int slot = 44;
        for (Empire emp : plugin.empireManager.getActiveEmpires()){
            if(!Objects.equals(empire,emp.getREFERENCE()) && emp.getIsAlive()){

                ItemStack empIcon = new ItemStack(Material.BEACON);
                ItemMeta empMeta = empIcon.getItemMeta();
                empMeta.setDisplayName(emp.getEmpireColor() + emp.getName() + "'s capital");
                empMeta.setLocalizedName(emp.getREFERENCE());
                List<String> empLore = new ArrayList<>();
                int d = (int) Math.sqrt(Math.pow(emp.getEmpireCaptureZone().getX()-playerX,2) + Math.pow(emp.getEmpireCaptureZone().getY() - playerY,2) + Math.pow(emp.getEmpireCaptureZone().getZ()-playerZ,2));
                empLore.add(ChatColor.GOLD + "" + d + " block away");
                if(emp.getCanCapture()){
                    empLore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Their capital can be captured!");
                }else{
                    empLore.add(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Capture their towns!");
                }
                empMeta.setLore(empLore);
                empIcon.setItemMeta(empMeta);
                inv.setItem(slot,empIcon);
                slot--;
            }

        }


        return  inv;
    }



}
