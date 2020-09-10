package com.tort.EmpireBattles.Game;

import com.tort.EmpireBattles.Main;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.*;
import java.util.logging.Level;

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
    private StatHandler statHandler;
    private double progressO = 0;
    private double progressM = 0;
    private double progressR = 0;
    private double progressV = 0;
    private final int capturetime = 15;
    private Main plugin;

    public CapZone(String town,Location capLocation, String zoneOwner , Main plugin){
        this.town = town;
        this.capLocation = capLocation;
        this.zoneOwner = zoneOwner;
        this.plugin = plugin;

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
            statHandler = plugin.getStatHandler();



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
                        if(progressM > 0 || progressR > 0 || progressV > 0){
                            bar.setColor(BarColor.BLUE);
                            bar.setTitle(ChatColor.WHITE + "Clearing Progress...");
                            double max = Math.max(Math.max(progressM,progressR),progressV);
                            bar.setProgress(max/capturetime);

                            progressM -= 2;
                            progressR -= 2;
                            progressV -= 2;
                        }else {
                            bar.setColor(BarColor.GREEN);
                            bar.setTitle(ChatColor.WHITE + town + " is secure!");
                            bar.setProgress(1.0);
                            progressO = 0;
                            progressM = 0;
                            progressR = 0;
                            progressV = 0;
                        }



                    } else {
                        if(progressO <= 0){
                            progressO = 0;
                            Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + "Ottomans are capturing " + town.toUpperCase());
                        }
                        bar.setColor(BarColor.BLUE);
                        bar.setTitle(ChatColor.YELLOW + "OTTOMANS " + ChatColor.DARK_AQUA + "ARE CAPTURING " + town );
                        progressO++;
                        double progress = abs(progressO) / capturetime;
                        bar.setProgress(progress);
                        if (progressO == capturetime) {
                            Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + "OTTOMANS HAVE CAPTURED " + town.toUpperCase() + " FROM " + zoneOwner.toUpperCase());
                            zoneOwner = "OTTOMANS";
                            spawnFireworks(capLocation, Color.YELLOW);
                            Main.CaptureOwners.put(town.toUpperCase(), "OTTOMANS");
                            plugin.changeTownColor(town.toUpperCase(), "OTTOMANS");
                            progressO = 0;
                            progressM = 0;
                            progressR = 0;
                            progressV = 0;
                            for(Player p: playersOnPoint){
                                String pTeam = Main.getTeam(p.getUniqueId().toString());
                                if (Objects.equals(pTeam, "OTTOMANS")) {
                                    statHandler.addCapture(p);
                                    statHandler.playerdata.saveConfig();
                                }
                            }
                        }
                    }


                } else if (MONGOLS > OTTOMANS && MONGOLS > ROMANS && MONGOLS > VIKINGS) { // If Mongols have the most people on the Capture point


                    if (Objects.equals(zoneOwner, "MONGOLS")) {
                        if(progressO > 0 || progressR > 0 || progressV > 0){
                            bar.setColor(BarColor.BLUE);
                            bar.setTitle(ChatColor.WHITE + "Clearing Progress...");
                            double max = Math.max(Math.max(progressO,progressR),progressV);
                            bar.setProgress(max/capturetime);
                            progressO -= 2;
                            progressR -= 2;
                            progressV -= 2;
                        }else {
                            bar.setColor(BarColor.GREEN);
                            bar.setTitle(ChatColor.WHITE + town + " is secure!");
                            bar.setProgress(1.0);
                            progressO = 0;
                            progressM = 0;
                            progressR = 0;
                            progressV = 0;
                        }


                    } else {
                        if(progressM <= 0){
                            progressM = 0;
                            Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + "Mongols are capturing " + town.toUpperCase());
                        }
                        bar.setColor(BarColor.BLUE);
                        bar.setTitle(ChatColor.DARK_BLUE + "MONGOLS " + ChatColor.DARK_AQUA + "ARE CAPTURING " + town );
                        progressM++;
                        double progress = abs(progressM) / capturetime;
                        bar.setProgress(progress);
                        if (progressM == capturetime) {
                            Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + "MONGOLS HAVE CAPTURED " + town.toUpperCase() + " FROM " + zoneOwner.toUpperCase());
                            zoneOwner = "MONGOLS";
                            spawnFireworks(capLocation, Color.BLUE);
                            Main.CaptureOwners.put(town.toUpperCase(), "MONGOLS");
                            plugin.changeTownColor(town.toUpperCase(), "MONGOLS");
                            progressO = 0;
                            progressM = 0;
                            progressR = 0;
                            progressV = 0;
                            for(Player p: playersOnPoint){
                                String pTeam = Main.getTeam(p.getUniqueId().toString());
                                if (Objects.equals(pTeam, "MONGOLS")) {
                                    statHandler.addCapture(p);
                                    statHandler.playerdata.saveConfig();
                                }
                            }

                        }
                    }


                } else if (ROMANS > OTTOMANS && ROMANS > MONGOLS && ROMANS > VIKINGS) { // If Romans have the most people on the Capture point

                    if (Objects.equals(zoneOwner, "ROMANS")) {
                        if(progressO > 0 || progressM > 0 || progressV > 0){
                            bar.setColor(BarColor.BLUE);
                            bar.setTitle(ChatColor.WHITE + "Clearing Progress...");
                            double max = Math.max(Math.max(progressO,progressM),progressV);

                            bar.setProgress(max/capturetime);
                            progressM -= 2;
                            progressO -= 2;
                            progressV -= 2;
                        }else {
                            bar.setColor(BarColor.GREEN);
                            bar.setTitle(ChatColor.WHITE + town + " is secure!");
                            bar.setProgress(1.0);
                            progressO = 0;
                            progressM = 0;
                            progressR = 0;
                            progressV = 0;
                        }

                    } else  {
                        if(progressR <= 0){
                            progressR = 0;
                            Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + "Romans are capturing " + town.toUpperCase());
                        }
                        bar.setColor(BarColor.BLUE);
                        bar.setTitle(ChatColor.DARK_RED + "ROMANS " + ChatColor.DARK_AQUA + "ARE CAPTURING " + town );
                        progressR++;
                        double progress = abs(progressR) / capturetime;
                        bar.setProgress(progress);
                        if (progressR == capturetime) {
                            Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + "ROMANS HAVE CAPTURED " + town.toUpperCase() + " FROM " + zoneOwner.toUpperCase());
                            zoneOwner = "ROMANS";
                            spawnFireworks(capLocation, Color.RED);
                            Main.CaptureOwners.put(town.toUpperCase(), "ROMANS");
                            plugin.changeTownColor(town.toUpperCase(), "ROMANS");
                            progressO = 0;
                            progressM = 0;
                            progressR = 0;
                            progressV = 0;
                            for(Player p: playersOnPoint){
                                String pTeam = Main.getTeam(p.getUniqueId().toString());
                                if (Objects.equals(pTeam, "ROMANS")) {
                                    statHandler.addCapture(p);
                                    statHandler.playerdata.saveConfig();
                                }
                            }

                        }
                    }


                } else if (VIKINGS > OTTOMANS && VIKINGS > MONGOLS && VIKINGS > ROMANS) { //If Vikings have the most people on the Capture point


                    if (Objects.equals(zoneOwner, "VIKINGS")) {
                        if(progressO > 0 || progressM > 0 || progressR > 0){
                            bar.setColor(BarColor.BLUE);
                            bar.setTitle(ChatColor.WHITE + "Clearing Progress...");
                            double max = Math.max(Math.max(progressO,progressM),progressR);
                            bar.setProgress(max/capturetime);
                            progressM -= 2;
                            progressO -= 2;
                            progressR -= 2;
                        }else {
                            bar.setColor(BarColor.GREEN);
                            bar.setTitle(ChatColor.WHITE + town + " is secure!");
                            bar.setProgress(1.0);
                            progressO = 0;
                            progressM = 0;
                            progressR = 0;
                            progressV = 0;
                        }



                    } else  {
                        if(progressV <= 0){
                            progressV = 0;
                            Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + "Vikings are capturing " + town.toUpperCase());
                        }
                        bar.setColor(BarColor.BLUE);
                        bar.setTitle(ChatColor.DARK_PURPLE + "VIKINGS " + ChatColor.DARK_AQUA + "ARE CAPTURING " + town );
                        progressV++;
                        double progress = abs(progressV) / capturetime;
                        bar.setProgress(progress);
                        if (progressV == capturetime) {
                            Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + "VIKINGS HAVE CAPTURED " + town.toUpperCase() + " FROM " + zoneOwner.toUpperCase());
                            zoneOwner = "VIKINGS";
                            spawnFireworks(capLocation, Color.PURPLE);
                            Main.CaptureOwners.put(town.toUpperCase(), "VIKINGS");
                            plugin.changeTownColor(town.toUpperCase(), "VIKINGS");
                            progressO = 0;
                            progressM = 0;
                            progressR = 0;
                            progressV = 0;
                            for(Player p: playersOnPoint){
                                String pTeam = Main.getTeam(p.getUniqueId().toString());
                                if (Objects.equals(pTeam, "VIKINGS")) {
                                    statHandler.addCapture(p);
                                    statHandler.playerdata.saveConfig();
                                }
                            }

                        }
                    }

                } else {//Contested
                    bar.setProgress(1.0);
                    bar.setColor(BarColor.RED);
                    bar.setTitle(ChatColor.RED + town + " is Contested!");
                }
            }
        }catch (Exception e){
            getLogger().log(Level.INFO, String.valueOf(e));
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

    private void spawnFireworks(Location location, Color color) {
        Location loc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
        loc.add(0, 6, 0);
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(5);
        fwm.addEffect(FireworkEffect.builder().withColor(color).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();



    }



    }
