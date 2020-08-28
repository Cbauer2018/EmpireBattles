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

    public GameInstance(Main plugin) {
        this.plugin = plugin;
    }

    public void start(){
        capZones.removeAll(capZones);
        empireCapZones.removeAll(empireCapZones);
        if(!Main.CaptureZones.isEmpty()){

            for(Map.Entry<String, Location> entry: Main.CaptureZones.entrySet()){ //Create class for every Cap Zone
                capZones.add(new CapZone(entry.getKey(), entry.getValue(), Main.CaptureOwners.get(entry.getKey())));

            }


            for (int i = 0; i < capZones.size(); i++) {
                capZones.get(i).setCaptureZone(capZones.get(i).getTown()); //Run /setCaptureZone for each zone
            }

            for(Map.Entry<String, Location> entry: Main.EmpireZones.entrySet()){ //Create class for every Empire Zone
                empireCapZones.add(new EmpireCapZone(entry.getKey(), entry.getValue(), Main.EmpireStatus.get(entry.getKey())));
            }
            for (int i = 0; i < empireCapZones.size(); i++) {
                empireCapZones.get(i).setCaptureZone(empireCapZones.get(i).getEmpire()); //Run /setCaptureZone for each zone
            }

            Collection<? extends Player> players = getServer().getOnlinePlayers();
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

                }
            };

            br.runTaskTimer(plugin, 0, 10);


        }


    }

    public ArrayList<CapZone> getCapZones(){
        return capZones;
    }

}
