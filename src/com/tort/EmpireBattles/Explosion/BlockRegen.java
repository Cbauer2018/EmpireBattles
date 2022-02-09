package com.tort.EmpireBattles.Explosion;

import com.tort.EmpireBattles.Empires.Empire;
import com.tort.EmpireBattles.Main;
import com.tort.EmpireBattles.Towns.Town;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockRegen extends BukkitRunnable {


    List<BlockState> updatedBlockList = new ArrayList<>();
    Town town;
    Empire empire;
    boolean isTown;


    public BlockRegen(List<Block> blocks , Town town , Empire empire){
        this.town = town;
        this.empire = empire;
        for(Block b : blocks){
            updatedBlockList.add(b.getState());

        }
    }

    @Override
    public void run() {
        if(town != null){
            if(!town.getGate().isGateDestroyed()){
                regen();
            }
        }
        if(empire != null){
            if(!empire.getGate().isGateDestroyed()){
                regen();
            }
        }


    }


    public void regen(){

        int max = updatedBlockList.size() - 1;
        if(max > -1){
            if(!updatedBlockList.get(max).getType().equals(Material.TNT)){
                updatedBlockList.get(max).update(true, false);

            }
            updatedBlockList.remove(max);

        } else {
            this.cancel();
        }
    }
}
