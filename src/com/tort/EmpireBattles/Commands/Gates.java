package com.tort.EmpireBattles.Commands;

import com.tort.EmpireBattles.Files.GateDataManager;
import com.tort.EmpireBattles.Files.TownDataManager;
import com.tort.EmpireBattles.Gates.Gate;
import com.tort.EmpireBattles.Main;
import com.tort.EmpireBattles.Towns.Town;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.StringJoiner;

public class Gates implements CommandExecutor {
    public GateDataManager gatedata;
    private Main plugin;


    public Gates(Main plugin){
        this.plugin = plugin;

    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)){
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

        if(command.getName().equalsIgnoreCase("gate")) { //If command is /gate
            if (strings[0].equals("create")) {
                if (!(strings.length == 2)) {
                    player.sendMessage(ChatColor.RED + "Invalid use of command. To create a gate use: /gate create <Town/Empire Reference>");
                    return true;
                }
                if (!Objects.equals(plugin.townManager.getTown(strings[1].toUpperCase()), null)) {
                    Gate gate = new Gate(strings[1].toUpperCase() , "town");

                    plugin.townManager.getTown(strings[1].toUpperCase()).setGate(gate);
                    player.sendMessage(ChatColor.BLUE + strings[1].toUpperCase() + "'s gate has been created!" + ChatColor.GOLD + " " + strings[1].toUpperCase() + ChatColor.BLUE + " is the reference for this gate.");
                    return true;
                }

                if (!Objects.equals(plugin.empireManager.getEmpire(strings[1].toUpperCase()), null)) {
                    Gate gate = new Gate(strings[1].toUpperCase() , "empire");
                    plugin.empireManager.getEmpire(strings[1].toUpperCase()).setGate(gate);
                    player.sendMessage(ChatColor.BLUE + strings[1].toUpperCase() + "'s gate has been created!" + ChatColor.GOLD + " " + strings[1].toUpperCase() + ChatColor.BLUE + " is the reference for this gate.");
                    return true;
                }


                player.sendMessage(ChatColor.RED + "Invalid Reference.");
                return true;
            }
        }

        if(strings[1].equals("set")) {
            Gate gate;

            if (!Objects.equals(plugin.townManager.getTown(strings[0].toUpperCase()), null)) {
                gate = plugin.townManager.getTown(strings[0].toUpperCase()).getGate();
            }else if (!Objects.equals(plugin.empireManager.getEmpire(strings[0].toUpperCase()), null)) {
                gate = plugin.empireManager.getEmpire(strings[0].toUpperCase()).getGate();
            }else{
                player.sendMessage(ChatColor.RED + "Invalid Reference.");
                return true;
            }


            if (strings[2].equals("cannon")) {
                    Location cannonLocation = player.getLocation().subtract(0, 1, 0);
                    gate.setCannonLocation(cannonLocation.getBlock().getLocation());
                    player.sendMessage(ChatColor.BLUE + "The cannon location for " + gate.getGateOwner() + " has been set!");
                    return true;
            }

            if (strings[2].equals("location")) {
                Location gateLocation = player.getLocation();
                gate.setGateLocation(gateLocation);
                player.sendMessage(ChatColor.BLUE + "The gate location for " + gate.getGateOwner() + " has been set!");
                return true;
            }
        }



        return true;
    }
}
