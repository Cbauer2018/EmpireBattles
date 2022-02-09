package com.tort.EmpireBattles.Game;

import com.tort.EmpireBattles.Empires.Empire;
import com.tort.EmpireBattles.Main;
import com.tort.EmpireBattles.Towns.Town;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class GameInstance {

    public ArrayList<CapZone> capZones = new ArrayList<>();
    public ArrayList<EmpireCapZone> empireCapZones = new ArrayList<>();
    public ArrayList<CannonEvent> cannonEvents = new ArrayList<>();
    private Main plugin;
    private ScoreboardInstance scoreboardInstance;
    private ArrayList<BukkitRunnable> runnables = new ArrayList<>();
    private boolean gameStarted = false;
    private GameState state = GameState.STOPPED;
    private boolean armorStandsSet = false;

    public GameInstance(Main plugin) {
        this.plugin = plugin;
    }




    public void start(){
        state = GameState.IN_PROCESS;
        gameStarted = true;
        scoreboardInstance = new ScoreboardInstance(plugin);
        scoreboardInstance.setScoreBoard();
        plugin.renderChecks.startRendering();

        if(!plugin.townManager.getActiveTowns().isEmpty()){

            for(Empire empire: plugin.empireManager.getActiveEmpires()){ //Create object for every Cap Zone
                EmpireCapZone zone = new EmpireCapZone(empire.getName(), empire.getREFERENCE(), empire.getEmpireCaptureZone(), empire.getIsAlive(), plugin);
                empireCapZones.add(zone);
                zone.setCaptureZone();
                if(!Objects.equals(empire.getGate(),null)){
                    CannonEvent cannonEvent = new CannonEvent(plugin,empire.getGate());
                    cannonEvents.add(cannonEvent);
                    cannonEvent.setGate();
                }
            }

            for(Town town: plugin.townManager.getActiveTowns()){ //Create object for every Cap Zone
                CapZone zone = new CapZone(town.getName(), town.getTownCaptureZone(), town.getOwner(),plugin , town.getType());
                capZones.add(zone);
                zone.setCaptureZone();
                if(!Objects.equals(town.getGate(),null)){
                    CannonEvent cannonEvent = new CannonEvent(plugin,town.getGate());
                    cannonEvents.add(cannonEvent);
                    cannonEvent.setGate();
                }
            }





            Bukkit.broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.AQUA + "" + ChatColor.BOLD + "The Game has begun! Conquer The World!");

            BukkitRunnable br = new BukkitRunnable() {
                @Override
                public void run() {

                    for (int counter = 0; counter < cannonEvents.size(); counter++) {
                        cannonEvents.get(counter).checkCannon();
                    }

                    for (int counter = 0; counter < empireCapZones.size(); counter++) {
                        empireCapZones.get(counter).checkCapture();
                    }
                    for (int counter = 0; counter < capZones.size(); counter++) {
                        capZones.get(counter).checkCapture();
                    }

                    scoreboardInstance.updateScoreboard();



                }
            };


            runnables.add(br);
            br.runTaskTimer(plugin, 0, 10);
        }


    }

    public void stop(){
        for(BukkitRunnable runnable : runnables){
            runnable.cancel();
        }
        capZones.removeAll(capZones);
        empireCapZones.removeAll(empireCapZones);
        cannonEvents.removeAll(cannonEvents);
        gameStarted = false;
        if(!Objects.equals(scoreboardInstance,null)) {
            scoreboardInstance.removeAllPlayers();
        }

        state = GameState.STOPPED;

        plugin.renderChecks.stopRendering();


    }

    public void pregame(){

        state = GameState.PREGAME;

        BukkitRunnable br = new BukkitRunnable() {
            int countdown = 60;

            @Override
            public void run() {
            if(countdown == 0){
                start();
                for(Player player: Bukkit.getOnlinePlayers()){
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);

                }
                this.cancel();
            }else{
                if(countdown == 60){
                    Bukkit.broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + "" + ChatColor.BOLD + " Game Starting in " + ChatColor.GREEN + "" + ChatColor.GREEN + "1" + ChatColor.GOLD + "" + ChatColor.BOLD + " minute");
                }else if(countdown%10 == 0){
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + "" + ChatColor.BOLD + " Game Starting in " + ChatColor.GREEN + "" + ChatColor.GREEN + countdown + ChatColor.GOLD + "" + ChatColor.BOLD + " seconds");

                }else if(countdown <= 9){
                    Bukkit.broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + "" + ChatColor.BOLD + " Game Starting in " + ChatColor.GREEN + "" + ChatColor.GREEN + countdown);
                    for(Player player: Bukkit.getOnlinePlayers()){
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);

                    }
                }

                countdown--;
            }

            }
        };
        br.runTaskTimer(plugin, 0, 20);



    }




    public ArrayList<CapZone> getCapZones(){
        return capZones;
    }


    public void setPlayerScoreBoard(Player player){
        scoreboardInstance.addPlayer(player);

    }
    public boolean isGameStarted(){
        return gameStarted;
    }

    public GameState getGameState(){return state;}

    public ScoreboardInstance getGameScoreboard(){return  scoreboardInstance;}

    public ArrayList<CannonEvent> getCannonEvents(){return  cannonEvents;}

}
