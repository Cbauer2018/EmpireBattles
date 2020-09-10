package com.tort.EmpireBattles.Game;

import com.tort.EmpireBattles.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class GameInstance {

    public ArrayList<CapZone> capZones = new ArrayList<>();
    public ArrayList<EmpireCapZone> empireCapZones = new ArrayList<>();
    private Main plugin;
    private ScoreboardInstance scoreboardInstance;
    private ArrayList<BukkitRunnable> runnables = new ArrayList<>();
    private boolean gameStarted = false;

    public GameInstance(Main plugin) {
        this.plugin = plugin;
    }

    public void start(){
        gameStarted = true;
        scoreboardInstance = new ScoreboardInstance(plugin);
        scoreboardInstance.setScoreBoard();

        Collection<? extends Player> players = getServer().getOnlinePlayers();
        for (Player player : players) {
            scoreboardInstance.addPlayer(player);
        }

        if(!Main.CaptureZones.isEmpty()){

            for(Map.Entry<String, Location> entry: Main.CaptureZones.entrySet()){ //Create class for every Cap Zone
                capZones.add(new CapZone(entry.getKey(), entry.getValue(), Main.CaptureOwners.get(entry.getKey()),plugin));

            }


            for (int i = 0; i < capZones.size(); i++) {
                capZones.get(i).setCaptureZone(capZones.get(i).getTown()); //Run /setCaptureZone for each zone
            }

            for(Map.Entry<String, Location> entry: Main.EmpireZones.entrySet()){ //Create class for every Empire Zone
                empireCapZones.add(new EmpireCapZone(entry.getKey(), entry.getValue(), Main.EmpireStatus.get(entry.getKey()), plugin));
            }
            for (int i = 0; i < empireCapZones.size(); i++) {
                empireCapZones.get(i).setCaptureZone(empireCapZones.get(i).getEmpire()); //Run /setCaptureZone for each zone
            }


            for(Player player : players){
                player.sendMessage(ChatColor.GREEN + "[Game Started]");
            }

            BukkitRunnable br = new BukkitRunnable() {
                @Override
                public void run() {
                    for (int counter = 0; counter < capZones.size(); counter++) {
                        capZones.get(counter).checkCapture(capZones.get(counter).getTown());
                    }
                    for (int counter = 0; counter < empireCapZones.size(); counter++) {
                        empireCapZones.get(counter).checkCapture(empireCapZones.get(counter).getEmpire());
                    }
                    scoreboardInstance.updateScoreboard();



                }
            };
            runnables.add(br);
            br.runTaskTimer(plugin, 0, 10);




        }


    }

    public void stop(){
        for(BukkitRunnable runnable : runnables){
            runnable.cancel();
        }
        capZones.removeAll(capZones);
        empireCapZones.removeAll(empireCapZones);
        gameStarted = false;
        scoreboardInstance.removeAllPlayers();
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.RED + " Game Stopped.");

    }

    public ArrayList<CapZone> getCapZones(){
        return capZones;
    }


    public void setPlayerScoreBoard(Player player){
        scoreboardInstance.addPlayer(player);

    }
    public boolean isGameStarted(){
        return gameStarted;
    }




}
