package com.tort.EmpireBattles.Game;

import com.tort.EmpireBattles.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.abs;
import static org.bukkit.Bukkit.getLogger;

public class CapZone  {
    private final String town;
    private final Location capLocation;
    private ArmorStand armorStand;
    private List<Player> lastCapping = new ArrayList<>();
    private BossBar bar;
    private String zoneOwner = "NEUTRAL";
    private String lastCapturingEmpire = "NEUTRAL";
    private double progressX = 0;
    private double progressY = 0;
    double time = 1.0 / (30);

    public CapZone(String town,Location capLocation, String zoneOwner){
        this.town = town;
        this.capLocation = capLocation;
        this.zoneOwner = zoneOwner;

    }



    public static Location getTownCap(String town ){
        return  Main.CaptureZones.get(town);
    }



    void setCaptureZone(String town){

            ArmorStand name;

            name = (ArmorStand) getTownCap(town).getWorld().spawnEntity(getTownCap(town), EntityType.ARMOR_STAND);
            name.setCustomName(town);

            name.setCustomNameVisible(true);
            name.setVisible(false);
            name.setGravity(false);
            name.setMarker(true);

            armorStand = name;
            bar = Bukkit.createBossBar(ChatColor.DARK_AQUA + "CAPTURING " + town, BarColor.BLUE, BarStyle.SOLID);
            bar.setVisible(true);
            bar.setProgress(0.0);




    }

    public void checkCapture(String town){

        try {
            int OTTOMANS = 0;
            int MONGOLS = 0;
            int ROMANS = 0;
            int VIKINGS = 0;


            final List<Player> playersOnPoint = new ArrayList<>(getNearbyPlayers());
            for (Player p : playersOnPoint) { //Add new players to bar
                if (!lastCapping.contains(p))
                    bar.addPlayer(p);
                String pTeam = Main.getTeam(p.getUniqueId().toString());
                if (Objects.equals(pTeam, "OTTOMANS")) {
                    OTTOMANS++;
                }
                if (Objects.equals(pTeam, "MONGOLS")) {
                    MONGOLS++;
                }
                if (Objects.equals(pTeam, "ROMANS")) {
                    ROMANS++;
                }
                if (Objects.equals(pTeam, "VIKINGS")) {
                    VIKINGS++;
                }
            }
            for (Player p : lastCapping) { // Remove previous players
                if (!playersOnPoint.contains(p))
                    bar.removePlayer(p);
            }
            lastCapping = new ArrayList<>(playersOnPoint);


            if (OTTOMANS > 0 || MONGOLS > 0 || ROMANS > 0 || VIKINGS > 0) {


                if (OTTOMANS > MONGOLS && OTTOMANS > ROMANS && OTTOMANS > VIKINGS) { //If Ottomans have the most people on the Capture point


                    if (Objects.equals(zoneOwner, "OTTOMANS")) {
                        bar.setColor(BarColor.GREEN);
                        bar.setTitle(ChatColor.WHITE + town + " is captured!");
                        bar.setProgress(1.0);
                        progressX = 0;
                        progressY = 0;
                    } else if (Objects.equals(lastCapturingEmpire, "OTTOMANS")) {
                        bar.setColor(BarColor.BLUE);
                        bar.setTitle(ChatColor.YELLOW + "OTTOMANS " + ChatColor.DARK_AQUA + "ARE CAPTURING " + town );
                        progressX++;
                        double progress = abs(progressX) / 15;
                        bar.setProgress(progress);
                        if (progressX == 15) {
                            zoneOwner = "OTTOMANS";
                            Main.CaptureOwners.put(town, "OTTOMANS");
                        }
                    } else {
                        bar.setColor(BarColor.BLUE);
                        bar.setTitle(ChatColor.YELLOW + "OTTOMANS " + ChatColor.DARK_AQUA + "ARE CAPTURING " + town );
                        progressX = 0;
                        progressY = 0;
                        progressX++;
                        double progress = abs(progressX) / 15;
                        bar.setProgress(progress);

                        lastCapturingEmpire = "OTTOMANS";
                    }


                } else if (MONGOLS > OTTOMANS && MONGOLS > ROMANS && MONGOLS > VIKINGS) { // If Mongols have the most people on the Capture point


                    if (Objects.equals(zoneOwner, "MONGOLS")) {
                        bar.setColor(BarColor.GREEN);
                        bar.setTitle(ChatColor.WHITE + town + " is captured!");
                        bar.setProgress(1.0);
                        progressX = 0;
                        progressY = 0;
                    } else if (Objects.equals(lastCapturingEmpire, "MONGOLS")) {
                        bar.setColor(BarColor.BLUE);
                        bar.setTitle(ChatColor.DARK_BLUE + "MONGOLS " + ChatColor.DARK_AQUA + "ARE CAPTURING " + town );
                        progressX--;
                        double progress = abs(progressX) / 15;
                        bar.setProgress(progress);
                        if (progressX == -15) {
                            zoneOwner = "MONGOLS";
                            Main.CaptureOwners.put(town, "MONGOLS");
                        }
                    } else {
                        bar.setColor(BarColor.BLUE);
                        bar.setTitle(ChatColor.DARK_BLUE + "MONGOLS " + ChatColor.DARK_AQUA + "ARE CAPTURING " + town );
                        progressX = 0;
                        progressY = 0;
                        progressX--;
                        double progress = abs(progressX) / 15;
                        bar.setProgress(progress);
                        lastCapturingEmpire = "MONGOLS";
                    }


                } else if (ROMANS > OTTOMANS && ROMANS > MONGOLS && ROMANS > VIKINGS) { // If Romans have the most people on the Capture point

                    if (Objects.equals(zoneOwner, "ROMANS")) {
                        bar.setColor(BarColor.GREEN);
                        bar.setTitle(ChatColor.WHITE + town + " is captured!");
                        bar.setProgress(1.0);
                        progressX = 0;
                        progressY = 0;
                    } else if (Objects.equals(lastCapturingEmpire, "ROMANS")) {
                        bar.setColor(BarColor.BLUE);
                        bar.setTitle(ChatColor.DARK_RED + "ROMANS " + ChatColor.DARK_AQUA + "ARE CAPTURING " + town );
                        progressY++;
                        double progress = abs(progressY) / 15;
                        bar.setProgress(progress);
                        if (progressY == 15) {
                            zoneOwner = "ROMANS";
                            Main.CaptureOwners.put(town, "ROMANS");
                        }
                    } else {
                        bar.setColor(BarColor.BLUE);
                        bar.setTitle(ChatColor.DARK_RED + "ROMANS " + ChatColor.DARK_AQUA + "ARE CAPTURING " + town );
                        progressX = 0;
                        progressY = 0;
                        progressY++;
                        double progress = abs(progressY) / 15;
                        bar.setProgress(progress);
                        lastCapturingEmpire = "ROMANS";
                    }


                } else if (VIKINGS > OTTOMANS && VIKINGS > MONGOLS && VIKINGS > ROMANS) { //If Vikings have the most people on the Capture point


                    if (Objects.equals(zoneOwner, "VIKINGS")) {
                        bar.setColor(BarColor.GREEN);
                        bar.setTitle(ChatColor.WHITE + town + " is captured!");
                        bar.setProgress(1.0);
                        progressX = 0;
                        progressY = 0;
                    } else if (Objects.equals(lastCapturingEmpire, "VIKINGS")) {
                        bar.setColor(BarColor.BLUE);
                        bar.setTitle(ChatColor.DARK_PURPLE + "VIKINGS " + ChatColor.DARK_AQUA + "ARE CAPTURING " + town );
                        progressY--;
                        double progress = abs(progressY) / 15;
                        bar.setProgress(progress);
                        if (progressY == -15) {
                            zoneOwner = "VIKINGS";
                            Main.CaptureOwners.put(town, "VIKINGS");
                        }
                    } else {

                        bar.setColor(BarColor.BLUE);
                        bar.setTitle(ChatColor.DARK_PURPLE + "VIKINGS " + ChatColor.DARK_AQUA + "ARE CAPTURING " + town );
                        progressX = 0;
                        progressY = 0;
                        progressY--;
                        double progress = abs(progressY) / 15;
                        bar.setProgress(progress);
                        lastCapturingEmpire = "VIKINGS";
                    }

                } else {//Contested
                    bar.setProgress(1.0);
                    bar.setColor(BarColor.RED);
                    bar.setTitle(ChatColor.RED + town + " is Contested!");
                }
            }
        }catch (Exception e){
            getLogger().log(Level.INFO, String.valueOf(e.getStackTrace()));
        }

    }

    public Location getCapLocation() {
        return capLocation;
    }

    public String getTown() {
        return town;
    }

    public String getZoneOwner(){
        return  zoneOwner;
    }

    public void setZoneOwner(String zoneOwner){
        this.zoneOwner = zoneOwner;
    }


    List<Player> getNearbyPlayers() {
        List<Player> pls = new ArrayList<>();

        for (Entity e : armorStand.getNearbyEntities(5, 2.5, 5)) {
            if (e instanceof Player) {
                pls.add((Player) e);
            }
        }

        return pls;
    }
}
