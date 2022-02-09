package com.tort.EmpireBattles.Commands;

import com.tort.EmpireBattles.Empires.Empire;
import com.tort.EmpireBattles.Files.EmpireDataManager;
import com.tort.EmpireBattles.Files.TownDataManager;
import com.tort.EmpireBattles.Main;
import com.tort.EmpireBattles.Towns.Town;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;
import java.util.StringJoiner;

public class Towns implements CommandExecutor {
    public TownDataManager towndata;
    private Main plugin;


    public Towns(Main plugin){
        this.plugin = plugin;

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof  Player)){
            return true;
        }
        Player player = (Player) commandSender;

        if(!player.hasPermission("empires.admin")){
            player.sendMessage(ChatColor.RED + "You do not have permission!");
            return true;
        }
        if(Objects.equals(strings.length,0)){
            player.sendMessage(ChatColor.RED + "Incorrect use.");
            return true;
        }
        this.towndata = new TownDataManager(plugin);
        if(command.getName().equalsIgnoreCase("town")) { //If command is /empire
            if(strings[0].equals("create")){
                if(strings.length == 2){
                    Town town = new Town(strings[1]);
                    town.setOwner("NEUTRAL");
                    plugin.townManager.addTown(town);
                    player.sendMessage(ChatColor.BLUE + strings[1].toUpperCase() + " has been created!" + ChatColor.GOLD + " " + strings[1].toUpperCase() + ChatColor.BLUE + " is the reference for this town.");
                    return true;
                }else{
                    player.sendMessage(ChatColor.RED + "Invalid use of command. To create a town use: /town create <Town Reference>");
                    return true;
                }
            }

            if(strings[0].equals("delete")){
                if(strings.length == 2){
                    plugin.townManager.removeTown(strings[1]);
                    if(this.towndata.getConfig().contains("Towns." + strings[1].toUpperCase())){
                        this.towndata.getConfig().set("Towns." + strings[1].toUpperCase(), null);
                        this.towndata.saveConfig();
                    }
                    player.sendMessage(ChatColor.RED + strings[1].toUpperCase() + " has been removed from towns.");
                }else{
                    player.sendMessage(ChatColor.RED + "Town not found. Use /town delete <Town Reference>");
                    return true;
                }
            }

            if(strings[0].equals("list")){
                ArrayList<Town> towns = plugin.townManager.getActiveTowns();
                player.sendMessage(ChatColor.GRAY+ "----*" + ChatColor.DARK_RED + "MC" + ChatColor.GOLD + "Empires" + ChatColor.GRAY+ "*----" );
                player.sendMessage(ChatColor.DARK_GRAY + "Town List: ");
                for(Town town : towns){
                    player.sendMessage(ChatColor.GOLD+ town.getREFERENCE());
                }
                return true;
            }

            if(strings[1].equals("set")) {
                if (Objects.equals(plugin.townManager.getTown(strings[0].toUpperCase()), null)) {
                    player.sendMessage(ChatColor.RED + "Invalid Town Reference.");
                    return true;
                }
                Town town = plugin.townManager.getTown(strings[0].toUpperCase());


                if(strings[2].equals("name")){
                    if(strings.length >= 4){
                        StringJoiner joiner = new StringJoiner(" ");
                        for(int i = 3; i < strings.length;i++){
                            joiner.add(strings[i]);
                        }
                        String name = joiner.toString();
                        town.setName(name);
                        player.sendMessage(ChatColor.BLUE + "Town " + town.getREFERENCE() + " name set to:" + ChatColor.GRAY + name);
                    }
                    return true;
                }

                if(strings[2].equals("capture")){
                        Location captureLocation = player.getLocation().add(0, 3, 0);
                        town.setTownCaptureZone(captureLocation);
                        player.sendMessage(ChatColor.BLUE + "The capture zone for " + town.getREFERENCE() + " has been set!");
                    return true;
                }

                if(strings[2].equals("spawn")){
                        Location townSpawn = new Location(player.getWorld(),player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),player.getLocation().getYaw(),player.getLocation().getPitch());
                        town.setTownSpawnPoint(townSpawn);
                        player.sendMessage(ChatColor.BLUE + "The spawn for " + town.getREFERENCE() + " has been set!");
                    return true;
                }

                if(strings[2].equals("defendspawn")){
                    Location loc = new Location(player.getWorld(),player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),player.getLocation().getYaw(),player.getLocation().getPitch());
                    town.setGateDestroyedSpawn(loc);
                    player.sendMessage(ChatColor.BLUE + "The defend spawn for " + town.getREFERENCE() + " has been set!");
                    return true;
                }

                if(strings[2].equals("type")){
                    if(strings.length == 4){
                        town.setType(strings[3].toUpperCase());
                        player.sendMessage(ChatColor.BLUE + "The town type for " + town.getREFERENCE() + " is set to: " + strings[3]);
                    }
                    return true;
                }


            }


            if(strings[1].equals("get")) {
                if (Objects.equals(plugin.townManager.getTown(strings[0].toUpperCase()), null)) {
                    player.sendMessage(ChatColor.RED + "Invalid Town Reference.");
                    return true;
                }
                Town town = plugin.townManager.getTown(strings[0].toUpperCase());


                if(strings[2].equals("name")){
                    player.sendMessage(ChatColor.BLUE + "Town " + town.getREFERENCE() + "'S name is: " + ChatColor.GRAY + town.getName());
                    return true;
                }

                if(strings[2].equals("type")){
                    player.sendMessage(ChatColor.BLUE + "The town type for " + town.getREFERENCE() + " is: " + town.getType());
                    return true;
                }


            }

            if(strings[1].equals("add")) {
                if (Objects.equals(plugin.townManager.getTown(strings[0].toUpperCase()), null)) {
                    player.sendMessage(ChatColor.RED + "Invalid Town Reference.");
                    return true;
                }
                Town town = plugin.townManager.getTown(strings[0].toUpperCase());


                if (strings[2].equals("wool")) {
                    Block block = plugin.getBlockLocation().getBlock();
                    town.addWoolBlock(block);
                    player.sendMessage(ChatColor.BLUE + "Wool block added to " + town.getREFERENCE());
                    return true;
                }
            }

            if(strings[1].equals("remove")) {
                if (Objects.equals(plugin.townManager.getTown(strings[0].toUpperCase()), null)) {
                    player.sendMessage(ChatColor.RED + "Invalid Town Reference.");
                    return true;
                }
                Town town = plugin.townManager.getTown(strings[0].toUpperCase());


                if (strings[2].equals("all")) {
                    town.clearBlocks();
                    player.sendMessage(ChatColor.BLUE + "Blocks cleared for " + town.getREFERENCE());
                    return true;
                }

                if (strings[2].equals("wool")) {
                    town.removeBlock(plugin.getBlockLocation().getBlock());
                    player.sendMessage(ChatColor.BLUE + "Block removed from " + town.getREFERENCE());
                    return true;
                }
            }

            player.sendMessage(ChatColor.RED + "Incorrect use.");
            return true;

        }


        return true;
    }
}
