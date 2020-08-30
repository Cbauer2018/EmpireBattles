package com.tort.EmpireBattles.Commands;

import com.tort.EmpireBattles.Files.EmpireDataManager;
import com.tort.EmpireBattles.Files.PlayerDataManager;

import com.tort.EmpireBattles.Files.TownDataManager;
import com.tort.EmpireBattles.Main;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class Empires implements CommandExecutor {
    public EmpireDataManager empiredata;
    public PlayerDataManager playerdata;
    private Main plugin;

    public Empires(Main plugin){
        this.plugin = plugin;

    }




    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;
        this.playerdata = new PlayerDataManager(plugin);
        this.empiredata = new EmpireDataManager(plugin);

        if(command.getName().equalsIgnoreCase("empire")){ //If command is /empire

            if(strings[0].equals("join")){ // If first argument is join in /empire [command]
                if(Objects.equals(strings[1].toUpperCase(),"OTTOMANS") || Objects.equals(strings[1].toUpperCase(),"MONGOLS") || Objects.equals(strings[1].toUpperCase(),"ROMANS") || Objects.equals(strings[1].toUpperCase(),"VIKINGS")) { //If second argument in /empire join [empire] equals an empire
                        if(!Objects.equals(Main.getStatus(strings[1].toUpperCase()), true)) { // If empire is not captured
                            joinEmpire(player, strings[1].toUpperCase()); // Join empire
                        }else{
                            player.sendMessage(ChatColor.RED + strings[1].toUpperCase() + " have been captured. Select another Empire"); //notify player empire is captured
                        }
                }else{
                    player.sendMessage(ChatColor.RED + "Invalid empire name."); //Notify player that the empire name is not valid
                }

            }else if(strings[1].equals("setCapture")){ // If first argument is "setCapture" in /empire [command]

                Main.EmpireZones.put(strings[0].toUpperCase(), player.getLocation().add(0,3,0));
                player.sendMessage(ChatColor.BLUE + "Capture Zone for " + strings[0] + " created!");
            }

        }

        //SET SPAWN COMMAND
        if(command.getName().equalsIgnoreCase("setSpawn")){ // if command is /setSpawn
            try{
                Main.setStatus(strings[0].toUpperCase(), false);
                empiredata.getConfig().set(strings[0].toUpperCase() + ".spawnpoint" + ".X", player.getLocation().getX());
                empiredata.getConfig().set(strings[0].toUpperCase() + ".spawnpoint" + ".Y", player.getLocation().getY());
                empiredata.getConfig().set(strings[0].toUpperCase() + ".spawnpoint" + ".Z", player.getLocation().getZ());    //loads spawn values into the file
                empiredata.getConfig().set(strings[0].toUpperCase() + ".spawnpoint" + ".yaw", player.getLocation().getYaw());
                empiredata.getConfig().set(strings[0].toUpperCase() + ".spawnpoint" + ".pitch", player.getLocation().getPitch());
                empiredata.saveConfig();
                Location empireSpawn = new Location(player.getWorld(),player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),player.getLocation().getYaw(),player.getLocation().getPitch());
                Main.EmpireSpawns.put(strings[0].toUpperCase(), empireSpawn);
                player.sendMessage("Spawn point for " + strings[0] + " has been set."); //notify player that the spawn point is set.
            }catch (Exception e){
                player.sendMessage("Invalid Empire");
                player.sendMessage(e.toString());
            }

        }


        //SPAWN COMMAND
        if(command.getName().equalsIgnoreCase("spawn")){ // /spawn for empire
            if(!Objects.equals(Main.playerTeams.get(player.getUniqueId().toString()), "NEUTRAL") && Main.playerTeams.containsKey(player.getUniqueId().toString())) { // If player isnt neutral and player is on team

                String empire = Main.playerTeams.get(player.getUniqueId().toString()); //empire of team
                Location location = Main.EmpireSpawns.get(empire); // Empire's spawn

                player.teleport(location); //teleport player

            }else{
                player.sendMessage(ChatColor.RED + "Join an empire to use /spawn!");
            }
        }
        return true;
    }


    public void joinEmpire(Player player, String empire){

        Main.setTeam(player, empire.toUpperCase());
        playerdata.getConfig().set("players." + player.getUniqueId().toString() + ".empire", empire.toUpperCase());
        playerdata.saveConfig();

        empiredata.getConfig().set(empire.toUpperCase() + ".players." + player.getUniqueId().toString() + ".player_name", player.getDisplayName());
        empiredata.saveConfig();

        player.getInventory().clear();
        ItemStack sword = new ItemStack(Material.STONE_SWORD, 1);
        ItemStack steak = new ItemStack(Material.COOKED_BEEF, 16);
        ItemStack bow = new ItemStack(Material.BOW, 1);
        ItemStack arrows = new ItemStack(Material.ARROW, 32);

        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, bow);
        player.getInventory().setItem(2, steak);
        player.getInventory().setItem(9, arrows);

        ItemStack[] armor = new ItemStack[4];
        armor[0] = new ItemStack(Material.LEATHER_BOOTS, 1);
        armor[1] = new ItemStack(Material.LEATHER_LEGGINGS, 1);
        armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        armor[3] = new ItemStack(Material.LEATHER_HELMET, 1);

        LeatherArmorMeta meta0 = (LeatherArmorMeta) armor[0].getItemMeta();
        LeatherArmorMeta meta1 = (LeatherArmorMeta) armor[1].getItemMeta();
        LeatherArmorMeta meta2 = (LeatherArmorMeta) armor[2].getItemMeta();
        LeatherArmorMeta meta3 = (LeatherArmorMeta) armor[3].getItemMeta();
        if (empire.toUpperCase().equals("OTTOMANS")) {
            player.setPlayerListName(ChatColor.YELLOW + "[Ottoman] " + ChatColor.WHITE + player.getName());
            player.setDisplayName(ChatColor.YELLOW + player.getName());
            meta0.setColor(Color.YELLOW);
            meta1.setColor(Color.YELLOW);
            meta2.setColor(Color.YELLOW);
            meta3.setColor(Color.YELLOW);
        } else if (empire.toUpperCase().equals("MONGOLS")) {
            player.setDisplayName(ChatColor.DARK_BLUE + player.getName());
            player.setPlayerListName(ChatColor.DARK_BLUE + "[Mongol] " + ChatColor.WHITE + player.getName());
            meta0.setColor(Color.BLUE);
            meta1.setColor(Color.BLUE);
            meta2.setColor(Color.BLUE);
            meta3.setColor(Color.BLUE);
        } else if (empire.toUpperCase().equals("ROMANS")) {
            player.setDisplayName(ChatColor.DARK_RED + player.getName());
            player.setPlayerListName(ChatColor.DARK_RED + "[Roman] " + ChatColor.WHITE + player.getName());
            meta0.setColor(Color.RED);
            meta1.setColor(Color.RED);
            meta2.setColor(Color.RED);
            meta3.setColor(Color.RED);
        } else {
            player.setDisplayName(ChatColor.DARK_PURPLE+ player.getName());
            player.setPlayerListName(ChatColor.DARK_PURPLE + "[Viking] " + ChatColor.WHITE + player.getName());
            meta0.setColor(Color.PURPLE);
            meta1.setColor(Color.PURPLE);
            meta2.setColor(Color.PURPLE);
            meta3.setColor(Color.PURPLE);
        }
        meta0.setUnbreakable(true);
        meta1.setUnbreakable(true);
        meta2.setUnbreakable(true);
        meta3.setUnbreakable(true);

        armor[0].setItemMeta(meta0);
        armor[1].setItemMeta(meta1);
        armor[2].setItemMeta(meta2);
        armor[3].setItemMeta(meta3);

        player.getInventory().setBoots(armor[0]);
        player.getInventory().setLeggings(armor[1]);
        player.getInventory().setChestplate(armor[2]);
        player.getInventory().setHelmet(armor[3]);
        player.updateInventory();

        Location location = Main.EmpireSpawns.get(empire.toUpperCase());
        player.teleport(location);


        player.sendMessage("You have joined the " + empireChatColor(empire.toUpperCase()) + empire.toUpperCase() + ChatColor.WHITE + " !");

    }

  public static ChatColor empireChatColor(String empire){
        if(Objects.equals(empire.toUpperCase(), "OTTOMANS")){
            return ChatColor.YELLOW;
        }

        if(Objects.equals(empire.toUpperCase(), "MONGOLS")){
            return ChatColor.DARK_BLUE;
        }

      if(Objects.equals(empire.toUpperCase(), "ROMANS")){
          return ChatColor.DARK_RED;
      }else{
          return ChatColor.DARK_PURPLE;
      }


  }

}
