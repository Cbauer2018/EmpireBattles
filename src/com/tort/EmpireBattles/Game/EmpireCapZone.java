package com.tort.EmpireBattles.Game;

import com.sun.org.apache.bcel.internal.generic.FALOAD;
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
import static org.bukkit.Bukkit.getServer;

public class EmpireCapZone {
    private final String empire;
    private final Location capLocation;
    private ArmorStand armorStand;
    private List<Player> lastCapping = new ArrayList<>();
    private BossBar bar;
    private String lastCapturingEmpire = "NEUTRAL";
    private boolean broadcastMessage = true;
    private boolean isCaptured = false;
    private double score = 0;

    double time = 1.0 / (30);

    public EmpireCapZone(String empire, Location capLocation, Boolean isCaptured){
        this.empire = empire;
        this.capLocation = capLocation;
        this.isCaptured = isCaptured;

    }



    public static Location getEmpireCap(String empire ){
        return  Main.EmpireZones.get(empire);
    }




    void setCaptureZone(String empire){
        if(!isCaptured) {
            ArmorStand name;

            name = (ArmorStand) getEmpireCap(empire).getWorld().spawnEntity(getEmpireCap(empire), EntityType.ARMOR_STAND);
            name.setCustomName(empire + " CAPTURE ZONE");

            name.setCustomNameVisible(true);
            name.setVisible(false);
            name.setGravity(false);
            name.setMarker(true);
            armorStand = name;
            bar = Bukkit.createBossBar(ChatColor.WHITE + " CAPTURE ZONE IS SECURE.", BarColor.GREEN, BarStyle.SOLID);
            bar.setVisible(true);
            bar.setProgress(1.0);
        }

    }

    public void checkCapture(String empire){
        if(!isCaptured) {

            for (Map.Entry<String, String> entry : Main.CaptureOwners.entrySet()) {  //Checks if Empire owns any towns. If no towns are owned empire can be captured
                if (Objects.equals(entry.getValue(), empire)) {
                    broadcastMessage = true;
                    for (Player p : lastCapping) { // Remove previous players
                        bar.removePlayer(p);
                    }
                    bar.setColor(BarColor.GREEN);
                    bar.setTitle(ChatColor.WHITE + " CAPTURE ZONE IS SECURE.");
                    bar.setProgress(1.0);
                    score = 0;

                    return;
                }

            }
            try {
                if (broadcastMessage) {
                    Collection<? extends Player> players = getServer().getOnlinePlayers();
                    for (Player player : players) {
                        player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + " THE " + empire + " CAN BE CAPTURED!");
                    }
                    broadcastMessage = false;
                }
                int EMPIRE = 0;
                int ENEMIES = 0;


                final List<Player> playersOnPoint = new ArrayList<>(getNearbyPlayers());
                for (Player p : playersOnPoint) { //Add new players to bar
                    if (!lastCapping.contains(p))
                        bar.addPlayer(p);
                    String pTeam = Main.getTeam(p.getUniqueId().toString());
                    if (Objects.equals(pTeam, empire)) {
                        EMPIRE++;
                    }
                    if (!Objects.equals(pTeam, empire)) {
                        ENEMIES++;
                    }

                }
                for (Player p : lastCapping) { // Remove previous players
                    if (!playersOnPoint.contains(p))
                        bar.removePlayer(p);
                }
                lastCapping = new ArrayList<>(playersOnPoint);
                if (!isCaptured) {
                    if (EMPIRE > 0 || ENEMIES > 0) {
                        if (EMPIRE > ENEMIES) {
                            bar.setColor(BarColor.GREEN);
                            bar.setTitle(ChatColor.WHITE + " CAPTURE ZONE IS SECURE.");
                            bar.setProgress(1.0);
                            score = 0;

                        } else if (ENEMIES > EMPIRE) {
                            bar.setColor(BarColor.BLUE);
                            bar.setTitle(ChatColor.DARK_AQUA + "CAPTURING THE " + empire);
                            score++;
                            double progress = abs(score) / 15;
                            bar.setProgress(progress);
                            if (score == 15) {
                                isCaptured = true;
                                bar.removeAll();
                                removeEmpire(empire);
                                Main.setStatus(empire, true);
                                spawnFireworks(capLocation);
                            }
                        } else if (EMPIRE == ENEMIES) {
                            bar.setProgress(1.0);
                            bar.setColor(BarColor.RED);
                            bar.setTitle(ChatColor.RED + "CAPTURE ZONE CONTESTED");
                        }
                    }
                }
            } catch (Exception e) {
                getLogger().log(Level.INFO, e.getCause().toString());
            }
        }
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
    public String getEmpire(){
        return empire;
    }

    private void spawnFireworks(Location location) {
        Location loc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
        loc.add(0, 6, 0);
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(10);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.GREEN).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();


    }

    public void removeEmpire(String empire){
        //Send all players of empire back to spawn
        //disable capture zone, maybe do if isCapture in checkCapture
        //change data of all players in empire safely

        Collection<? extends Player> players = getServer().getOnlinePlayers();
        for (Player player : players) {
            player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + empire + " HAVE BEEN ELIMINATED!" );
           if(Objects.equals(Main.getTeam(player.getUniqueId().toString()), empire)){
               Main.setTeam(player,"NEUTRAL");
               player.getInventory().clear();
               player.teleport(Bukkit.getServer().getWorld("world").getSpawnLocation());
               player.sendMessage(ChatColor.RED + "Your empire has been captured..." + ChatColor.GREEN + "Choose another empire!");
           }
        }


    }

    }
