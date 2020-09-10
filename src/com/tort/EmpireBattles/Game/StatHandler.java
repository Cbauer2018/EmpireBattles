package com.tort.EmpireBattles.Game;
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
        this.playerdata = new PlayerDataManager(this.plugin);
    }

    public void addKill(Player p){
        if (!playerdata.getConfig().contains("players." + p.getUniqueId() + ".kills")) {
            playerdata.getConfig().set("players." + p.getUniqueId() + ".kills", 0);
        }
        if (!playerdata.getConfig().contains("players." + p.getUniqueId() + ".gamekills")) {
            playerdata.getConfig().set("players." + p.getUniqueId() +".gamekills", 0);
        }


        int kills = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".kills");
        int gamekills = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".gamekills");
        kills++;
        gamekills++;

        playerdata.getConfig().set("players." + p.getUniqueId() + ".kills", kills);
        playerdata.getConfig().set("players." + p.getUniqueId() +".gamekills", gamekills);

    }


    public void addDeath(Player p){
        if (!playerdata.getConfig().contains("players." + p.getUniqueId() + ".deaths")) {
            playerdata.getConfig().set("players." + p.getUniqueId() + ".deaths", 0);
        }
        if (!playerdata.getConfig().contains("players." + p.getUniqueId() + ".gamedeaths")) {
            playerdata.getConfig().set("players." + p.getUniqueId() +".gamedeaths", 0);
        }


        int deaths = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".deaths");
        int gamedeaths = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".gamedeaths");
        deaths++;
        gamedeaths++;

        playerdata.getConfig().set("players." + p.getUniqueId() + ".deaths", deaths);
        playerdata.getConfig().set("players." + p.getUniqueId() +".gamedeaths", gamedeaths);
    }

    public void addCapture(Player p){
        if (!playerdata.getConfig().contains("players." + p.getUniqueId() + ".captures")) {
            playerdata.getConfig().set("players." + p.getUniqueId() + ".captures", 0);
        }
        if (!playerdata.getConfig().contains("players." + p.getUniqueId() + ".gamecaptures")) {
            playerdata.getConfig().set("players." + p.getUniqueId() +".gamecaptures", 0);
        }


        int captures = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".captures");
        int gamecaptures = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".gamecaptures");
        captures++;
        gamecaptures++;

        playerdata.getConfig().set("players." + p.getUniqueId() + ".captures", captures);
        playerdata.getConfig().set("players." + p.getUniqueId() +".gamecaptures", gamecaptures);

    }

    public Integer getTotalKills(Player p){
        if (!playerdata.getConfig().contains("players." + p.getUniqueId() + ".kills")) {
            return 0;
        }

        int kills = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".kills");
        return kills;

    }

    public Integer getTotalCaptures(Player p){
        if (!playerdata.getConfig().contains("players." + p.getUniqueId() + ".captures")) {
            return 0;
        }

        int captures =  playerdata.getConfig().getInt("players." + p.getUniqueId() + ".captures");
        return captures;
    }

    public Double getTotalKD(Player p){
        if (!playerdata.getConfig().contains("players." + p.getUniqueId() + ".kills")) {
            return 0D;
        }
        if (!playerdata.getConfig().contains("players." + p.getUniqueId() + ".deaths")) {
            int kills = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".kills");
            return (double) kills;
        }
        if(playerdata.getConfig().getInt("players." + p.getUniqueId() + ".deaths") == 0){
            int kills = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".kills");
            return (double) kills;
        }

            int kills = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".kills");
            int deaths = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".deaths");

            double KD = (double) kills/ (double) deaths;
            return KD;


    }

    public Integer getGameKills(Player p){
        if (!playerdata.getConfig().contains("players." + p.getUniqueId() + ".gamekills")) {
            return 0;
        }

        int kills = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".gamekills");
        return kills;

    }
    public Integer getGameCaptures(Player p){
        if (!playerdata.getConfig().contains("players." + p.getUniqueId() + ".gamecaptures")) {
            return 0;
        }

        int captures =  playerdata.getConfig().getInt("players." + p.getUniqueId() + ".gamecaptures");
        return captures;

    }
    public Double getGameKD(Player p){
        if (!playerdata.getConfig().contains("players." + p.getUniqueId() + ".gamekills")) {
            return 0D;
        }
        if (!playerdata.getConfig().contains("players." + p.getUniqueId() + ".gamedeaths")) {
            int kills = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".gamekills");
            return (double) kills;
        }

        if(playerdata.getConfig().getInt("players." + p.getUniqueId() + ".gamedeaths") == 0){
            int kills = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".gamekills");
            return (double) kills;
        }


        int kills = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".gamekills");
        int deaths = playerdata.getConfig().getInt("players." + p.getUniqueId() + ".gamedeaths");

        double KD = (double) kills/ (double) deaths;
        return KD;
    }

    public void removeGameKills(String uuid){
        if (!playerdata.getConfig().contains("players." + uuid + ".gamekills")) {
            return;
        }
        playerdata.getConfig().set("players." + uuid + ".gamekills",0);

    }
    public void removeGameDeaths(String uuid){
        if (!playerdata.getConfig().contains("players." + uuid + ".gamedeaths")) {
            return;
        }
        playerdata.getConfig().set("players." + uuid + ".gamedeaths",0);
    }

    public void removeGameCaptures(String uuid){
        if (!playerdata.getConfig().contains("players." + uuid + ".gamecaptures")) {
            return;
        }

        playerdata.getConfig().set("players." + uuid + ".gamecaptures",0);

    }

    public void resetPlayerTeam(String uuid){
        if (!playerdata.getConfig().contains("players." + uuid + ".empire")) {
            return;
        }

        playerdata.getConfig().set("players." + uuid + ".empire","NEUTRAL");

    }

    public void showStats(Player p){
        p.sendMessage(ChatColor.GRAY+ "----*" + ChatColor.RED + "MC" + ChatColor.GOLD + "Empires" + ChatColor.GRAY+ "*----" );
        p.sendMessage(ChatColor.GOLD + p.getName() + " Stats:");
        p.sendMessage(ChatColor.GOLD + "Total Kills: " + ChatColor.GRAY + getTotalKills(p));
        p.sendMessage(ChatColor.GOLD + "Total Captures: " + ChatColor.GRAY + getTotalCaptures(p));
        p.sendMessage(ChatColor.GOLD + "K/D Ratio: " + ChatColor.GRAY + getTotalKD(p));
        p.sendMessage(ChatColor.GOLD + "Current Game Kills: " + ChatColor.GRAY + getGameKills(p));
        p.sendMessage(ChatColor.GOLD + "Current Game Captures: " + ChatColor.GRAY + getGameCaptures(p));
        p.sendMessage(ChatColor.GOLD + "Current Game K/D: " + ChatColor.GRAY + getGameKD(p));
    }
    public void resetGameStats(String uuid){
        removeGameCaptures(uuid);
        removeGameKills(uuid);
        removeGameDeaths(uuid);
    }

    public void SaveData(){
        playerdata.saveConfig();
        playerdata.reloadConfig();
    }



}
