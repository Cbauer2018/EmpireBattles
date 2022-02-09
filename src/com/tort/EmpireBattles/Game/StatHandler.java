package com.tort.EmpireBattles.Game;
import com.tort.EmpireBattles.EPlayer.EPlayer;
import com.tort.EmpireBattles.Files.PlayerDataManager;
import com.tort.EmpireBattles.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class StatHandler {
   Main plugin;
   PlayerDataManager playerdata;


    public StatHandler(Main plugin){
        this.plugin = plugin;
    }



    public void showStats(Player p){
        String prefix = plugin.getPlayerPrefix(p);
        EPlayer ePlayer = plugin.ePlayerManager.getEPlayer(p);
        p.sendMessage(ChatColor.GRAY+ "----*" + ChatColor.DARK_RED + "MC" + ChatColor.GOLD + " Empires" + ChatColor.GRAY+ "*----" );
        p.sendMessage( prefix + ChatColor.WHITE + " " + p.getName() + ChatColor.GOLD + " Stats:");
        p.sendMessage(ChatColor.GOLD + "Total Kills: " + ChatColor.GRAY + ePlayer.getTotalKills());
        p.sendMessage(ChatColor.GOLD + "Total Captures: " + ChatColor.GRAY + ePlayer.getTotalCaptures());
        p.sendMessage(ChatColor.GOLD + "K/D Ratio: " + ChatColor.GRAY + ePlayer.getTotalKD());
        p.sendMessage(ChatColor.GOLD + "Current Game Kills: " + ChatColor.GRAY + ePlayer.getGameKills());
        p.sendMessage(ChatColor.GOLD + "Current Game Captures: " + ChatColor.GRAY + ePlayer.getGameCaptures());
        p.sendMessage(ChatColor.GOLD + "Current Game K/D: " + ChatColor.GRAY + ePlayer.getGameKD());
    }




}
