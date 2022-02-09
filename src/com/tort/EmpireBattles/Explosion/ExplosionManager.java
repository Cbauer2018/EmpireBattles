package com.tort.EmpireBattles.Explosion;

import com.tort.EmpireBattles.Empires.Empire;
import com.tort.EmpireBattles.Main;
import com.tort.EmpireBattles.Towns.Town;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ExplosionManager implements Listener {
    Main plugin;

    public ExplosionManager(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onExplodeEntity(EntityExplodeEvent e) {
        Creeper creeper = (Creeper) e.getEntity();
        String name = creeper.getCustomName();
        HashMap<Material, Location> blocksDamaged = new HashMap<>();
        List<Block> blocks = e.blockList();
        for(Block block : blocks){
            blocksDamaged.put(block.getType(),block.getLocation());
        }
        for (Town town : plugin.townManager.getActiveTowns()) {
            if (town.getGate() != null) {
                if (Objects.equals(name, town.getREFERENCE())) {
                    e.setYield(0);
                    new BlockRegen(blocks , town , null).runTaskTimer(plugin,1,1);
                    return;
                }
            }
        }

        for (Empire empire : plugin.empireManager.getActiveEmpires()) {
            if (empire.getGate() != null) {
                if (Objects.equals(name, empire.getREFERENCE())) {
                    e.setYield(0);
                    new BlockRegen(blocks , null, empire).runTaskTimer(plugin,1,1);
                    return;
                }
            }
        }
    }
}


