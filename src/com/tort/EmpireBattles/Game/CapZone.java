package com.tort.EmpireBattles.Game;

import com.tort.EmpireBattles.EPlayer.EPlayer;
import com.tort.EmpireBattles.Empires.Empire;
import com.tort.EmpireBattles.Files.Colors;
import com.tort.EmpireBattles.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.*;
import java.util.logging.Level;

import static java.lang.Math.abs;
import static org.bukkit.Bukkit.getLogger;

public class CapZone  {
    private final String town;
    private final Location capLocation;
    private final String type;
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
    private Map<String,Integer> empireScore = new HashMap<>();
    private final int capturetime = 15;
    private Main plugin;

    public CapZone(String town,Location capLocation, String zoneOwner , Main plugin, String type){
        this.town = town;
        this.capLocation = capLocation;
        this.zoneOwner = zoneOwner;
        this.plugin = plugin;
        this.type = type;
    }



    public static Location getTownCap(String town ){
        return  Main.CaptureZones.get(town);
    }



    void setCaptureZone(){
        capLocation.getChunk().load();
        for (Entity e : Objects.requireNonNull(capLocation.getWorld()).getNearbyEntities(capLocation,5,2.5,5)) { //removes previous armorstand
            if(!(e instanceof Player)) {
                e.remove();
            }
            }

            ArmorStand name;
            name = (ArmorStand) capLocation.getWorld().spawnEntity(capLocation, EntityType.ARMOR_STAND);
            name.setCustomName(town);

            name.setCustomNameVisible(true);
            name.setVisible(false);
            name.setGravity(false);
            name.setMarker(true);

        capLocation.getChunk().unload();


            armorStand = name;
            bar = Bukkit.createBossBar(ChatColor.DARK_AQUA + "CAPTURING " + town, BarColor.BLUE, BarStyle.SOLID);
            bar.setVisible(true);
            bar.setProgress(0.0);

            for(Empire empire: plugin.empireManager.getActiveEmpires()){
            empireScore.put(empire.getREFERENCE(),0);
            }





    }

    public void checkCapture(){

        Map<String,Integer> empirePlayersOnPoint = new HashMap<>();
        for(Empire empire: plugin.empireManager.getActiveEmpires()){
            empirePlayersOnPoint.put(empire.getREFERENCE(),0);
        }
        try {
            final List<Player> playersOnPoint = new ArrayList<>(getNearbyPlayers());
            for (Player p : playersOnPoint) { //Add new players to bar
                String pEmpire = plugin.ePlayerManager.getEPlayer(p).getEPlayerEmpire();
                if (!lastCapping.contains(p) && !Objects.equals("NEUTRAL",pEmpire))
                    bar.addPlayer(p);
                if(empirePlayersOnPoint.containsKey(pEmpire)){
                    int i =empirePlayersOnPoint.get(pEmpire);
                    i++;
                    empirePlayersOnPoint.replace(pEmpire,i);
                }

            }
            for (Player p : lastCapping) { // Remove previous players
                if (!playersOnPoint.contains(p))
                    bar.removePlayer(p);
            }
            lastCapping = new ArrayList<>(playersOnPoint);

            Map.Entry<String,Integer> maxPlayersOnPoint = null;

            for(Map.Entry<String,Integer> entry : empirePlayersOnPoint.entrySet()){
                if(maxPlayersOnPoint == null || entry.getValue().compareTo(maxPlayersOnPoint.getValue()) > 0){
                    maxPlayersOnPoint = entry;
                }
            }

            if(maxPlayersOnPoint.getValue() == 0){
                return;
            }

            boolean isContested = false;
            for(Map.Entry<String,Integer> entry : empirePlayersOnPoint.entrySet()){
                if (!entry.getKey().equals(maxPlayersOnPoint.getKey()) && entry.getValue().equals(maxPlayersOnPoint.getValue())) {
                    isContested = true;
                    break;
                }
            }

            if(isContested){
                bar.setProgress(1.0);
                bar.setColor(BarColor.RED);
                bar.setTitle(ChatColor.RED + town + " is Contested!");
                return;
            }

            if(Objects.equals(zoneOwner,maxPlayersOnPoint.getKey())){
                boolean isThereProgress = false;
                for(Map.Entry<String,Integer> progress:empireScore.entrySet()){
                    if(!progress.getKey().equals(zoneOwner) && progress.getValue() > 0){
                        isThereProgress = true;
                        break;
                    }
                }

                if(isThereProgress){
                    Map.Entry<String,Integer> maxProgress = null;
                    for(Map.Entry<String,Integer> entry : empireScore.entrySet()){
                        if((maxProgress == null || entry.getValue().compareTo(maxProgress.getValue()) > 0) && !entry.getKey().equals(zoneOwner)){
                            maxProgress = entry;
                        }
                    }
                    bar.setColor(BarColor.BLUE);
                    bar.setTitle(ChatColor.WHITE + "Clearing Progress...");
                    double max = maxProgress.getValue();
                    bar.setProgress(max/capturetime);

                    empireScore.replaceAll((key,value) -> value -= 2);
                    return;
                }
                plugin.townManager.getTown(town).getGate().setHasProgress(false);
                plugin.townManager.getTown(town).getGate().setNewOwner(false);
                bar.setColor(BarColor.GREEN);
                bar.setTitle(ChatColor.WHITE + town + " is secure!");
                bar.setProgress(1.0);
                empireScore.replaceAll((key,value) -> value = 0);
                return;
            }

            Empire empire = plugin.empireManager.getEmpire(maxPlayersOnPoint.getKey());
            String zoneOwnerName = "Neutral";
            ChatColor zoneOwnerColor = ChatColor.WHITE;
            if(!zoneOwner.equals("NEUTRAL")){
                Empire zoneOwnerEmpire = plugin.empireManager.getEmpire(zoneOwner);
                zoneOwnerName = zoneOwnerEmpire.getName();
                zoneOwnerColor = zoneOwnerEmpire.getEmpireColor();
            }

           if(!plugin.townManager.getTown(town).getGate().isGateDestroyed()){
               bar.setColor(BarColor.RED);
               bar.setTitle(ChatColor.WHITE + "The gate must be destroyed to capture!");
               bar.setProgress(1.0);
               return;
           }


            if(empireScore.get(maxPlayersOnPoint.getKey()) <= 0){
                Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + empire.getEmpireColor() + empire.getName() + ChatColor.GOLD + " is capturing " + zoneOwnerColor + town);
                empireScore.replace(maxPlayersOnPoint.getKey(),0);
            }
            plugin.townManager.getTown(town).getGate().setHasProgress(true);
            bar.setColor(BarColor.BLUE);
            bar.setTitle(empire.getEmpireColor() + empire.getREFERENCE() + ChatColor.DARK_AQUA + " ARE CAPTURING " + town );
            int score = empireScore.get(maxPlayersOnPoint.getKey());
            score++;
            empireScore.replace(maxPlayersOnPoint.getKey(),score);

            double progress = (double) score/ (double) capturetime;
            bar.setProgress(progress);
            if (score == capturetime) {
                Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + empire.getEmpireColor() + empire.getName() + ChatColor.GOLD +  " has captured " + empire.getEmpireColor() + town + ChatColor.GOLD + " from " + zoneOwnerColor + zoneOwnerName);
                zoneOwner = empire.getREFERENCE();
                spawnFireworks(capLocation, Colors.translateChatColorToColor(empire.getEmpireColor()));
                plugin.townManager.getTown(town).setOwner(empire.getREFERENCE());
                empireScore.replaceAll((key,value) -> value = 0);
                plugin.townManager.getTown(town).getGate().setHasProgress(false);
                plugin.townManager.getTown(town).getGate().setNewOwner(true);
                for(Player p: playersOnPoint){
                    EPlayer ePlayer = plugin.ePlayerManager.getEPlayer(p);
                    String playerEmpire = ePlayer.getEPlayerEmpire();
                    if(Objects.equals(playerEmpire,empire.getREFERENCE())){
                        int TotalCaptures = ePlayer.getTotalCaptures();
                        int GameCaptures = ePlayer.getGameCaptures();
                        TotalCaptures++;
                        GameCaptures++;
                        ePlayer.setTotalCaptures(TotalCaptures);
                        ePlayer.setGameCaptures(GameCaptures);
                        p.sendMessage(ChatColor.GREEN + "+10 gold for capturing a town!");
                        p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,10));
                    }
                }
                for(Block block : plugin.townManager.getTown(town).getWoolBlocks()){
                    block.setType(Colors.translateChatColorToWool(empire.getEmpireColor()));
                }
                plugin.changeTownColor(town,empire.getREFERENCE());
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

        for (Entity e : armorStand.getNearbyEntities(3, 2.5, 3)) {
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
