package com.tort.EmpireBattles.Game;

import com.nametagedit.plugin.NametagEdit;
import com.sun.org.apache.bcel.internal.generic.FALOAD;
import com.tort.EmpireBattles.Items.EmpireGUI;
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
    private Main plugin;
    private StatHandler statHandler;
    private final int capturetime = 15;

    double time = 1.0 / (30);

    public EmpireCapZone(String empire, Location capLocation, Boolean isCaptured , Main plugin){
        this.empire = empire;
        this.capLocation = capLocation;
        this.isCaptured = isCaptured;
        this.plugin = plugin;

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
            statHandler = plugin.getStatHandler();
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
                            if(score > 0){
                                bar.setColor(BarColor.BLUE);
                                bar.setTitle(ChatColor.WHITE + "Clearing Progress...");
                                bar.setProgress(score/capturetime);
                                score -= 2;

                            }else {
                                bar.setColor(BarColor.GREEN);
                                bar.setTitle(ChatColor.WHITE + "The Capital is secure!");
                                bar.setProgress(1.0);
                               score = 0;
                            }
                        } else if (ENEMIES > EMPIRE) {
                            bar.setColor(BarColor.BLUE);
                            bar.setTitle(ChatColor.DARK_AQUA + "CAPTURING THE " + empire);
                            score++;
                            double progress = abs(score) / capturetime;
                            bar.setProgress(progress);
                            if (score == capturetime) {
                                isCaptured = true;
                                bar.removeAll();
                                removeEmpire(empire);
                                Main.setStatus(empire, true); //Set status to true. True == Captured
                                spawnFireworks(capLocation);
                                for(Player p: playersOnPoint){
                                    String pTeam = Main.getTeam(p.getUniqueId().toString());
                                    if (!Objects.equals(pTeam, empire.toUpperCase())) {
                                        statHandler.addCapture(p);
                                        statHandler.playerdata.saveConfig();
                                    }
                                }

                                statHandler.SaveData();
                                if(Main.isGameDone()){
                                    endGame();
                                }
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
               player.getEnderChest().clear();
               player.teleport(Bukkit.getServer().getWorld("world").getSpawnLocation());
               player.sendMessage(ChatColor.RED + "Your empire has been captured..." + ChatColor.GREEN + "Choose another empire!");
               String prefix = plugin.getPlayerPrefix(player);
               NametagEdit.getApi().setNametag(player,prefix + " " + ChatColor.WHITE ,   null  );
               player.getInventory().setItem(0, EmpireGUI.EmpireGUI);
               player.setPlayerListName(prefix + ChatColor.WHITE + player.getName());


           }
        }


    }

    private void endGame(){
        String winner = Main.getWinner();


        if(Objects.equals(winner,"OTTOMANS")){

                Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.YELLOW + winner + ChatColor.WHITE + " HAVE WON THE GAME!");

        }else if(Objects.equals(winner,"MONGOLS")){

            Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.DARK_BLUE + winner + ChatColor.WHITE + " HAVE WON THE GAME!");

        }else if(Objects.equals(winner,"ROMANS")){

            Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.DARK_RED + winner + ChatColor.WHITE + " HAVE WON THE GAME!");


        }else{

            Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.DARK_PURPLE + winner + ChatColor.WHITE + " HAVE WON THE GAME!");

        }


        Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GREEN+ "THANK YOU FOR PLAYING!" + ChatColor.GOLD + " The server will be shutting down in 30 seconds.");



        Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getServer().setWhitelist(true), 600);


        Collection<? extends Player> players = getServer().getOnlinePlayers();
        for (Player player : players) {
            if(!Bukkit.getServer().getWhitelistedPlayers().contains(player)){
                Bukkit.getScheduler().runTaskLater(plugin, () -> player.kickPlayer("Thank you for playing!"), 605);
            }
        }









    }

    }
