package com.tort.EmpireBattles.Game;

import com.nametagedit.plugin.NametagEdit;
import com.sun.org.apache.bcel.internal.generic.FALOAD;
import com.tort.EmpireBattles.EPlayer.EPlayer;
import com.tort.EmpireBattles.Empires.Empire;
import com.tort.EmpireBattles.Items.EmpireGUI;
import com.tort.EmpireBattles.Main;
import com.tort.EmpireBattles.Towns.Town;
import com.tort.TortMessages;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import javax.mail.internet.AddressException;
import java.util.*;
import java.util.logging.Level;

import static java.lang.Math.abs;
import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class EmpireCapZone {
    private final String empire;
    private final String REFERENCE;
    private final Location capLocation;
    private ArmorStand armorStand;
    private List<Player> lastCapping = new ArrayList<>();
    private BossBar bar;
    private String lastCapturingEmpire = "NEUTRAL";
    private boolean broadcastMessage = true;
    private boolean isAlive = true;
    private double score = 0;
    private Main plugin;
    private StatHandler statHandler;
    private final int capturetime = 15;
    private Empire captureEmpire;
    private boolean firstBroadcast = true;

    double time = 1.0 / (30);

    public EmpireCapZone(String empire, String REFERENCE, Location capLocation, Boolean isAlive , Main plugin){
        this.empire = empire;
        this.capLocation = capLocation;
        this.isAlive = isAlive;
        this.plugin = plugin;
        this.REFERENCE = REFERENCE;
        captureEmpire = plugin.empireManager.getEmpire(REFERENCE);

    }







    void setCaptureZone(){

        if(captureEmpire.getIsAlive()) {

            capLocation.getChunk().load();
            for (Entity e : Objects.requireNonNull(capLocation.getWorld()).getNearbyEntities(capLocation,5,2.5,5)) { //removes previous armorstand
                if(!(e instanceof Player)) {
                    e.remove();
                }
            }


            ArmorStand name;
            name = (ArmorStand) capLocation.getWorld().spawnEntity(capLocation, EntityType.ARMOR_STAND);
            name.setCustomName(empire + " capture zone");

            name.setCustomNameVisible(true);
            name.setVisible(false);
            name.setGravity(false);
            name.setMarker(true);

            armorStand = name;

            capLocation.getChunk().unload();

            bar = Bukkit.createBossBar(ChatColor.DARK_AQUA + "CAPTURING " + empire, BarColor.BLUE, BarStyle.SOLID);
            bar.setVisible(true);
            bar.setProgress(0.0);


        }

    }

    public void checkCapture(){
        if(captureEmpire.getIsAlive()) {
                for(Town town : plugin.townManager.getActiveTowns()){
                    if(Objects.equals(town.getOwner(), REFERENCE)){
                        broadcastMessage = true;
                        for (Player p : lastCapping) { // Remove previous players
                            bar.removePlayer(p);
                        }
                        captureEmpire.setCanCapture(false);
                        plugin.empireManager.getEmpire(REFERENCE).getGate().setHasProgress(false);
                        plugin.empireManager.getEmpire(REFERENCE).getGate().setNewOwner(false);
                        bar.setColor(BarColor.GREEN);
                        bar.setTitle(ChatColor.WHITE + " CAPTURE ZONE IS SECURE.");
                        bar.setProgress(1.0);
                        score = 0;
                        firstBroadcast = false;
                        return;
                    }
                }

            try {

                if (broadcastMessage && !firstBroadcast) {

                    captureEmpire.setCanCapture(true);
                    Collection<? extends Player> players = getServer().getOnlinePlayers();
                    for (Player player : players) {
                        player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + captureEmpire.getEmpireColor() + captureEmpire.getName() + "'s"+ ChatColor.GOLD + " capital can be captured!");
                    }
                    broadcastMessage = false;
                }



                captureEmpire.setCanCapture(true);
                int EMPIRE = 0;
                int ENEMIES = 0;


                final List<Player> playersOnPoint = new ArrayList<>(getNearbyPlayers());
                for (Player p : playersOnPoint) { //Add new players to bar
                    String pTeam = plugin.ePlayerManager.getEPlayer(p).getEPlayerEmpire();
                    if (!lastCapping.contains(p)  && !Objects.equals("NEUTRAL",pTeam))
                        bar.addPlayer(p);
                    if (Objects.equals(pTeam, REFERENCE)) {
                        EMPIRE++;
                    }
                    if (!Objects.equals(pTeam, REFERENCE) && !Objects.equals("NEUTRAL",pTeam)) {
                        ENEMIES++;
                    }

                }
                for (Player p : lastCapping) { // Remove previous players
                    if (!playersOnPoint.contains(p))
                        bar.removePlayer(p);
                }
                lastCapping = new ArrayList<>(playersOnPoint);
                if (isAlive) {
                    if (EMPIRE > 0 || ENEMIES > 0) {
                        if (EMPIRE > ENEMIES) {
                            if(score > 0){
                                bar.setColor(BarColor.BLUE);
                                bar.setTitle(ChatColor.WHITE + "Clearing Progress...");
                                bar.setProgress(score/capturetime);
                                score -= 2;

                            }else {
                                plugin.empireManager.getEmpire(REFERENCE).getGate().setHasProgress(false);
                                plugin.empireManager.getEmpire(REFERENCE).getGate().setNewOwner(false);
                                bar.setColor(BarColor.GREEN);
                                bar.setTitle(ChatColor.WHITE + "The Capital is secure!");
                                bar.setProgress(1.0);
                               score = 0;
                            }
                        } else if (ENEMIES > EMPIRE) {
                            if(!plugin.empireManager.getEmpire(REFERENCE).getGate().isGateDestroyed()){
                                bar.setColor(BarColor.RED);
                                bar.setTitle(ChatColor.WHITE + "The gate must be destroyed to capture!");
                                bar.setProgress(1.0);
                                return;
                            }

                            plugin.empireManager.getEmpire(REFERENCE).getGate().setHasProgress(true);
                            bar.setColor(BarColor.BLUE);
                            bar.setTitle(ChatColor.DARK_AQUA + "Capturing " + captureEmpire.getEmpireColor() + captureEmpire.getName());

                            if(score == 0){
                                Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + captureEmpire.getEmpireColor() + captureEmpire.getName() + ChatColor.GOLD + " is being captured!");
                            }

                            score++;
                            double progress = abs(score) / capturetime;
                            bar.setProgress(progress);
                            if (score == capturetime) {
                                isAlive = false;
                                bar.removeAll();
                                removeEmpire(captureEmpire);
                                captureEmpire.setAlive(false);
                                spawnFireworks(capLocation);
                                score = 0;
                                plugin.empireManager.getEmpire(REFERENCE).getGate().setHasProgress(false);
                                plugin.empireManager.getEmpire(REFERENCE).getGate().setNewOwner(true);
                                plugin.setDynmapIconFire(captureEmpire);
                                for(Player p: playersOnPoint){
                                    EPlayer ePlayer = plugin.ePlayerManager.getEPlayer(p);
                                    String playerEmpire = ePlayer.getEPlayerEmpire();
                                    if(!Objects.equals(playerEmpire, REFERENCE)){
                                        int TotalCaptures = ePlayer.getTotalCaptures();
                                        int GameCaptures = ePlayer.getGameCaptures();
                                        TotalCaptures++;
                                        GameCaptures++;
                                        ePlayer.setTotalCaptures(TotalCaptures);
                                        ePlayer.setGameCaptures(GameCaptures);
                                        p.sendMessage(ChatColor.GREEN + "+30 gold for capturing an empire!");
                                        p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,30));
                                    }
                                }

                                if(isGameDone()){
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

    public void removeEmpire(Empire empire){
        //Send all players of empire back to spawn
        //disable capture zone, maybe do if isCapture in checkCapture
        //change data of all players in empire safely
        empire.getEmpirePlayerList().clear();
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        for (Player player : players) {
            player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + empire.getEmpireColor() + empire.getName() + ChatColor.GOLD + " has been eliminated!" );
           if( Objects.equals(plugin.ePlayerManager.getEPlayer(player).getEPlayerEmpire(), REFERENCE)){
               plugin.ePlayerManager.getEPlayer(player).setEPlayerEmpire("NEUTRAL");
               player.getInventory().clear();
               player.getEnderChest().clear();
               player.teleport(Bukkit.getServer().getWorld("hubmap").getSpawnLocation());
               player.sendMessage(ChatColor.RED + "Your empire has been captured..." + ChatColor.GREEN + "Choose another empire!");
               String prefix = plugin.getPlayerPrefix(player);
               NametagEdit.getApi().setNametag(player,prefix + " " + ChatColor.WHITE ,   null  );
               player.getInventory().setItem(0, EmpireGUI.EmpireGUI);
               player.setPlayerListName(prefix + ChatColor.WHITE + player.getName());


           }
        }


    }

    private void endGame()  {
        Empire winner = getWinner();

        Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + winner.getEmpireColor() + "" + ChatColor.BOLD + winner.getName() + ChatColor.WHITE + "" + ChatColor.BOLD + " has won empire battles!");
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            plugin.resetGame();
            plugin.gameInstance.pregame();
        }, 200);
    }

    public  Boolean isGameDone(){
        int empiresAlive = 0;
        for(Empire e : plugin.empireManager.getActiveEmpires()){
            if(e.getIsAlive()){
                empiresAlive++;
            }
        }

        if(empiresAlive == 1){
            return true;
        }
        return false;
    }

    public Empire getWinner(){
        for(Empire e : plugin.empireManager.getActiveEmpires()){
            if(e.getIsAlive()){
                return e;
            }
        }
        return  null;
    }

    }
