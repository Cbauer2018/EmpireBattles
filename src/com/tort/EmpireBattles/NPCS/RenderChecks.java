package com.tort.EmpireBattles.NPCS;

import com.tort.EmpireBattles.Empires.Empire;
import com.tort.EmpireBattles.Game.*;
import com.tort.EmpireBattles.Main;
import com.tort.EmpireBattles.Towns.Town;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class RenderChecks {

        private Main plugin;
        private ArrayList<BukkitRunnable> runnables = new ArrayList<>();
        private ArrayList<UpdateRenders> renders = new ArrayList<>();

        NPC NPC;

        public RenderChecks(Main plugin, NPC NPC) {
            this.plugin = plugin;
            this.NPC = NPC;
        }




        public void startRendering(){

            for(EntityPlayer npc : NPC.getNPCs()){
                UpdateRenders render = new UpdateRenders(npc,NPC);
                renders.add(render);
            }

                BukkitRunnable br = new BukkitRunnable() {
                    @Override
                    public void run() {

                        for (int counter = 0; counter < renders.size(); counter++) {
                            renders.get(counter).render();
                        }

                    }
                };


                runnables.add(br);
                br.runTaskTimer(plugin, 0, 20);
            }

        public void stopRendering(){
            for(BukkitRunnable runnable : runnables){
                runnable.cancel();
            }
        }


        }

