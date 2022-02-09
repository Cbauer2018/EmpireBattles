package com.tort.EmpireBattles.Game;

import com.tort.EmpireBattles.Gates.Gate;
import com.tort.EmpireBattles.Main;
import org.bukkit.scheduler.BukkitRunnable;

import javax.xml.stream.Location;
import java.util.ArrayList;

public class CannonEvent  {
    private Gate gate;
    Main plugin;
    private boolean cannonAlive = true;
    private ArrayList<BukkitRunnable> runnables = new ArrayList<>();

    public CannonEvent(Main plugin , Gate gate){
        this.gate = gate;
        this.plugin = plugin;
    }

    public void spawnCannon(){

        BukkitRunnable br = new BukkitRunnable() {
            @Override
            public void run() {

            }
        };
        runnables.add(br);
        br.runTaskTimer(plugin, 0, 10);


    }

}
