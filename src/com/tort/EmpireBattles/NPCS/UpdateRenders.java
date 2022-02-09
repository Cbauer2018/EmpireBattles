package com.tort.EmpireBattles.NPCS;

import com.tort.EmpireBattles.Files.EmpireUtils;
import com.tort.EmpireBattles.Main;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class UpdateRenders {

    private EntityPlayer npc;
    private NPC NPC;
    private List<Player> lastPlayersRendered = new ArrayList<>();
    public UpdateRenders(EntityPlayer npc, NPC NPC){
        this.npc = npc;
        this.NPC = NPC;
    }


    public void render(){

        Location l = new Location(npc.getBukkitEntity().getWorld(), npc.getBukkitEntity().getLocation().getX(), npc.getBukkitEntity().getLocation().getY(),npc.getBukkitEntity().getLocation().getZ());
        List<Player> players = new ArrayList<>();

        for (org.bukkit.entity.Entity e : EmpireUtils.getEntitiesAroundPoint(l,30)) {
            if (e instanceof Player) {
                players.add((Player) e);
            }
        }
        if(!players.isEmpty()){
            for(Player p : players) {
                if(!lastPlayersRendered.contains(p)) {
                    NPC.addSingleNPCPackets(p, npc);
                }
            }
            lastPlayersRendered = players;
        }

    }





}
