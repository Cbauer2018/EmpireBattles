package com.tort.EmpireBattles.Game;

import com.sun.javafx.stage.ScreenHelper;
import com.tort.EmpireBattles.Empires.Empire;
import com.tort.EmpireBattles.Explosion.BlockRegen;
import com.tort.EmpireBattles.Gates.Gate;
import com.tort.EmpireBattles.Main;
import com.tort.EmpireBattles.Towns.Town;
import dev.esophose.playerparticles.particles.ParticleEffect;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Math.abs;

public class CannonEvent  {
    private Gate gate;
    private int timer = 60;
    private int delay = 0;
    Main plugin;
    private ArmorStand armorStand;
    private  ArmorStand healthStand;
    Location particleLocation;
    Location armorStandLocation;
    Location healthLocation;
    Location cannonLocation;
    private boolean broadcast;


    public CannonEvent(Main plugin , Gate gate){
        this.gate = gate;
        this.plugin = plugin;
    }

    public void setGate(){

        particleLocation = new Location(gate.getCannonLocation().getWorld(), gate.getCannonLocation().getX(), gate.getCannonLocation().getY() + 2 ,gate.getCannonLocation().getZ());
        armorStandLocation = new Location(gate.getCannonLocation().getWorld(), gate.getCannonLocation().getX(), gate.getCannonLocation().getY() + 3 ,gate.getCannonLocation().getZ());
        healthLocation = new Location(gate.getCannonLocation().getWorld(), gate.getCannonLocation().getX(), gate.getCannonLocation().getY() + 2 ,gate.getCannonLocation().getZ());
        cannonLocation = new Location(gate.getCannonLocation().getWorld(), gate.getCannonLocation().getX(), gate.getCannonLocation().getY() + 1 ,gate.getCannonLocation().getZ());
        armorStandLocation.getChunk().setForceLoaded(true);
        gate.getGateLocation().getChunk().setForceLoaded(true);


        healthLocation.getChunk().load();
        for (Entity e : Objects.requireNonNull(armorStandLocation.getWorld()).getNearbyEntities(armorStandLocation,5,2.5,5)) { //removes previous armorstand
            if(!(e instanceof Player)){
                e.remove();
            }

        }
        int cannonX = gate.getCannonLocation().getBlock().getX();
        int cannonZ = gate.getCannonLocation().getBlock().getZ();
        int gateX = gate.getGateLocation().getBlock().getX();
        int gateZ = gate.getGateLocation().getBlock().getZ();
        ArmorStand name;
        name = (ArmorStand) armorStandLocation.getWorld().spawnEntity(armorStandLocation, EntityType.ARMOR_STAND);
        name.setCustomName(gate.getGateOwner() + " cannon location");
        name.setCustomNameVisible(true);
        name.setVisible(false);
        name.setGravity(false);
        name.setMarker(true);
        armorStand = name;



        ArmorStand health;

        health = (ArmorStand) armorStandLocation.getWorld().spawnEntity(healthLocation, EntityType.ARMOR_STAND);
        health.setCustomNameVisible(false);
        health.setVisible(false);
        health.setGravity(false);
        health.setMarker(true);
        healthStand= health;



        if(cannonX > gateX){
            particleLocation.add(0,0,0.6);
            particleLocation.add(0.5,0,0);
        }else if (cannonX < gateX){
            particleLocation.add(0,0,0.5);
            particleLocation.subtract(0.1,0,0);
        }

        if(cannonZ > gateZ){
            particleLocation.add(0,0,0.6);
            particleLocation.add(0.5,0,0);
        }else if(cannonZ < gateZ){
            particleLocation.add(0.5,0,0);
            particleLocation.subtract(0,0,0.1);
        }
        broadcast = true;
    }

    public void checkCannon(){
        if(!gate.isGateDestroyed()) {
            if(gate.isCannonDead()){
                gate.setCannonPlaced(false);
                cannonLocation.getBlock().setType(Material.AIR);
                Bukkit.getServer().getWorld("world").spawnParticle(Particle.EXPLOSION_HUGE,cannonLocation, 0);
                armorStand.setCustomName(gate.getGateOwner() + " cannon location");
                timer = 60;
                gate.setCannonHealth(100);
                healthStand.setCustomNameVisible(false);
                broadcast = true;

            }

            if (gate.isCannonPlaced()) {


                armorStand.setCustomNameVisible(true);
                healthStand.setCustomNameVisible(true);
                armorStand.setCustomName(ChatColor.WHITE + "Firing in: " + ChatColor.DARK_RED + timer/2);
                healthStand.setCustomName("" + ChatColor.GREEN + gate.getCannonHealth() + " HP");


                Bukkit.getServer().getWorld("world").spawnParticle(Particle.SOUL_FIRE_FLAME, particleLocation, 0);

                if(broadcast){
                    cannonPlacedBroadcast();
                }
                timer--;
                if (timer <= 0) {
                    armorStand.setCustomNameVisible(false);
                    gate.setCannonPlaced(false);
                    timer = 60;
                    healthStand.setCustomNameVisible(false);
                    Bukkit.getWorld(cannonLocation.getWorld().getName()).playSound(cannonLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    destroyGate();
                    broadcast = true;
                }
            }
        }else{
            if(!gate.isNewOwner()) {
                delay++;
                if (delay >= 60) {
                    if (!gate.isHasProgress()) {
                        armorStand.setCustomName(gate.getGateOwner() + " cannon location");
                        armorStand.setCustomNameVisible(true);
                        gate.setGateDestroyed(false);
                        delay = 0;
                        cannonLocation.getBlock().setType(Material.AIR);
                        gate.setCannonHealth(100);

                    }
                }
            }else{
                gate.setNewOwner(false);
                armorStand.setCustomName(gate.getGateOwner() + " cannon location");
                armorStand.setCustomNameVisible(true);
                gate.setGateDestroyed(false);
                timer = 60;
                delay = 0;
                cannonLocation.getBlock().setType(Material.AIR);
                gate.setCannonHealth(100);

            }
        }
    }

    private void destroyGate(){
        gate.setGateDestroyed(true);
            Location location = gate.getGateLocation();
        Creeper creeper = location.getWorld().spawn(location, Creeper.class);
        creeper.setCustomName(gate.getGateOwner());
        creeper.setMaxFuseTicks(0);
        creeper.setPowered(true);
        creeper.setExplosionRadius(5);


    }

    private void cannonPlacedBroadcast(){
        if(Objects.equals(gate.getType(),"town")){
            Town town = plugin.townManager.getTown(gate.getGateOwner());
            if(!Objects.equals(town.getOwner(),"NEUTRAL")){
                Empire e = plugin.empireManager.getEmpire(town.getOwner());
                for(Player player : e.getEmpirePlayerList()){
                    player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] "  + ChatColor.GOLD + " A cannon has been placed at " + e.getEmpireColor() + town.getName()   + ChatColor.GOLD + "!");

                }
            }
        }else{
            Empire e = plugin.empireManager.getEmpire(gate.getGateOwner());
            for(Player player : e.getEmpirePlayerList()){
                player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] "  + ChatColor.GOLD + " A cannon has been placed at your empire's capital!");

            }

        }
        broadcast = false;
    }



}
