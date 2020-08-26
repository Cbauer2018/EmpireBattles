package com.tort.EmpireBattles.Game;

import com.tort.EmpireBattles.Main;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Map;

public class GameInstance {

    public ArrayList<CapZone> capZones = new ArrayList<>();
    private Main plugin;

    public GameInstance(Main plugin) {
        this.plugin = plugin;
    }

    public void start(){
        if(!Main.CaptureZones.isEmpty()){
            for(Map.Entry<String, Location> entry: Main.CaptureZones.entrySet()){
                capZones.add(new CapZone(entry.getKey(), entry.getValue(), Main.CaptureOwners.get(entry.getKey())));

            }

            for (int i = 0; i < capZones.size(); i++) {
                capZones.get(i).setCaptureZone(capZones.get(i).getTown());
            }




            BukkitRunnable br = new BukkitRunnable() {
                @Override
                public void run() {
                    for (int counter = 0; counter < capZones.size(); counter++) {
                        capZones.get(counter).checkCapture(capZones.get(counter).getTown());
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
