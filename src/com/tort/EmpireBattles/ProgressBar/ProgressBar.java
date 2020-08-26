//package com.tort.EmpireBattles.ProgressBar;
//
//
//import com.tort.EmpireBattles.Commands.Towns;
//import com.tort.EmpireBattles.Files.EmpireDataManager;
//
//import com.tort.EmpireBattles.Files.PlayerDataManager;
//import com.tort.EmpireBattles.Files.TownDataManager;
//import com.tort.EmpireBattles.Main;
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.boss.BarColor;
//import org.bukkit.boss.BarStyle;
//import org.bukkit.boss.BossBar;
//import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.configuration.file.YamlConfiguration;
//import org.bukkit.entity.Player;
//import org.bukkit.metadata.FixedMetadataValue;
//import org.bukkit.scheduler.BukkitRunnable;
//import org.bukkit.scheduler.BukkitTask;
//
//import java.io.File;
//import java.util.*;
//
//
//public class ProgressBar {
//
//
//    private final Map<String, Integer> capturingEmpires = new HashMap<>();
//
//    private int taskID;
//    private final Main plugin;
//    private BossBar bar;
//    private EmpireDataManager empiredata;
//    private TownDataManager towndata;
//    private PlayerDataManager playerdata;
//
//
//    public ProgressBar(Main plugin) {
//        this.plugin = plugin;
//    }
//
//
//    public void addPlayer(Player player) {
//        bar.addPlayer(player);
//    }
//
//    public boolean barCreated = false;
//
//    public BossBar getBar() {
//        return bar;
//    }
//
//    public void createBar(Player player, Boolean isCapturing, String town) {
//        try {
//            this.playerdata = new PlayerDataManager(plugin);
//            this.empiredata = new EmpireDataManager(plugin);
//            this.towndata = new TownDataManager(plugin);
//            Boolean Contested = false;
//
//            taskID = towndata.getConfig().getInt("towns." + town + ".taskID");
//            String playerEmpire = playerdata.getConfig().get("players." + player.getUniqueId().toString() + ".empire").toString();
//
//
//            if (!barCreated && isCapturing) { //If bar is not yet created, create the bar. Set barCreated true in command so multiple bars are not created.
//
//                if (!empiredata.getConfig().contains(playerEmpire + ".towns." + town)) { //If path exists. Won't exist if empire hasn't captured any towns
//                    // if town is not captured by empire
//                    player.sendTitle(ChatColor.DARK_PURPLE + town, null, 5, 20, 5);
//                    bar = Bukkit.createBossBar(ChatColor.DARK_AQUA + "CAPTURING " + town, BarColor.BLUE, BarStyle.SOLID);
//                    bar.setVisible(true);
//                    if (capturingEmpires.containsKey(playerEmpire)) {
//                        return;
//                    }
//                    capturingEmpires.put(playerEmpire, cast(player, town));
//                } else { //If town is already captured by empire
//                    if (!Contested) {
//                        bar = Bukkit.createBossBar(ChatColor.WHITE + town + " is Captured!", BarColor.GREEN, BarStyle.SOLID);
//                        bar.setVisible(true);
//                        bar.setProgress(1.0);
//                        towndata.getConfig().set("towns." + town + ".progress", 0);
//                        towndata.saveConfig();
//                    } else {
//                        bar = Bukkit.createBossBar(ChatColor.RED + town + "Contested.", BarColor.RED, BarStyle.SOLID);
//                        bar.setVisible(true);
//                        bar.setProgress(1.0);
//                    }
//
//
//                }
//                bar.addPlayer(player);
//                barCreated = true;
//            }
//            if (!isCapturing) { // Not in Capture Zone
//
//
//                this.playerdata.getConfig().set("players." + player.getUniqueId().toString() + ".isCapturing", "false");
//                this.playerdata.saveConfig();
//
//                this.towndata.getConfig().set("towns." + town + ".isCapturing." + player.getUniqueId().toString(), null);
//                this.towndata.saveConfig();
//                this.towndata.reloadConfig();
//                String str = this.towndata.getConfig().getString("towns." + town + ".isCapturing");
//                if (str == null || str.replaceAll(" ", "").isEmpty()) {
//                    Bukkit.getScheduler().cancelTask(capturingEmpires.get(playerEmpire));
//                }
//
//                bar.removePlayer(player);
//                bar.setVisible(false);
//                barCreated = false;
//
//
//            }
//        } catch (Exception e) {
//            player.sendMessage(Arrays.toString(e.getStackTrace()));
//        }
//    }
//
//    public int cast(Player player, String town) {
//        this.empiredata = new EmpireDataManager(plugin);
//        this.towndata = new TownDataManager(plugin);
//        double[] progress = {0};
//
//        towndata.reloadConfig();
//        if (this.towndata.getConfig().contains("towns." + town + ".progress")) {
//            progress[0] = Double.parseDouble(this.towndata.getConfig().get("towns." + town + ".progress").toString());
//
//        } else {
//
//            this.towndata.getConfig().set("towns." + town + ".progress", 0);
//            this.towndata.saveConfig();
//
//        }
//
//        return new BukkitRunnable() {
//            double time = 1.0 / (30);
//            boolean dataSaved = false;
//
//            @Override
//            public void run() {
//
//                if (progress[0] >= 1.0) {
//                    if (!dataSaved) {
//                        bar.setTitle(ChatColor.WHITE + town + " is Captured!");
//                        bar.setColor(BarColor.GREEN);
//                        progress[0] = 1.0;
//                        Towns t = new Towns(plugin);
//                        t.townCaptured(player, town);
//                        Bukkit.getScheduler().cancelTask(taskID);
//                        dataSaved = true;
//                    }
//
//
//                } else {
//                    progress[0] = progress[0] + time;
//                    towndata.getConfig().set("towns." + town + ".progress", progress[0]);
//                    towndata.saveConfig();
//                    towndata.reloadConfig();
//                    player.sendMessage("After Save -" + towndata.getConfig().getString("towns." + town + ".progress"));
//                }
//
//
//                bar.setProgress(progress[0]);
//
//            }
//        }.runTaskTimer(plugin, 0, 10).getTaskId();
//
//
//    }
//}
//


//package com.tort.EmpireBattles.ProgressBar;
//
//
//import com.tort.EmpireBattles.Commands.Towns;
//import com.tort.EmpireBattles.Files.EmpireDataManager;
//
//import com.tort.EmpireBattles.Files.PlayerDataManager;
//import com.tort.EmpireBattles.Files.TownDataManager;
//import com.tort.EmpireBattles.Main;
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.boss.BarColor;
//import org.bukkit.boss.BarStyle;
//import org.bukkit.boss.BossBar;
//import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.configuration.file.YamlConfiguration;
//import org.bukkit.entity.Player;
//import org.bukkit.metadata.FixedMetadataValue;
//import org.bukkit.scheduler.BukkitRunnable;
//import org.bukkit.scheduler.BukkitTask;
//
//import java.io.File;
//import java.util.*;
//
//
//public class ProgressBar {
//
//
//    private final Map<String, Integer> capturingEmpires = new HashMap<>();
//
//    //private int taskID;
//    private final Main plugin;
//    private BossBar bar;
//    private EmpireDataManager empiredata;
//    private TownDataManager towndata;
//    private PlayerDataManager playerdata;
//
//
//    public ProgressBar(Main plugin) {
//        this.plugin = plugin;
//    }
//
//
//    public void addPlayer(Player player) {
//        bar.addPlayer(player);
//    }
//
//    public boolean barCreated = false;
//
//    public BossBar getBar() {
//        return bar;
//    }
//
//    public void createBar(Player player, Boolean isCapturing, String town) {
//        try {
//            player.sendMessage("Attempting to createBar");
//            this.playerdata = new PlayerDataManager(plugin);
//            this.empiredata = new EmpireDataManager(plugin);
//            this.towndata = new TownDataManager(plugin);
//            Boolean Contested = false;
//
//
//            String playerEmpire = playerdata.getConfig().get("players." + player.getUniqueId().toString() + ".empire").toString();
//            if (!barCreated && isCapturing) { //If bar is not yet created, create the bar. Set barCreated true in command so multiple bars are not created.
//
//                if (!empiredata.getConfig().contains(playerEmpire + ".towns." + town)) { //If path exists. Won't exist if empire hasn't captured any towns
//                    // if town is not captured by empire
//                    player.sendTitle(ChatColor.DARK_PURPLE + town, null, 5, 20, 5);
//                    bar = Bukkit.createBossBar(ChatColor.DARK_AQUA + "CAPTURING " + town, BarColor.BLUE, BarStyle.SOLID);
//                    bar.setVisible(true);
//                    if (capturingEmpires.containsKey(playerEmpire)) {
//                        return;
//                    }
//                    capturingEmpires.put(playerEmpire, cast(player, town, playerEmpire));
//                } else { //If town is already captured by empire
//                    if (!Contested) {
//                        bar = Bukkit.createBossBar(ChatColor.WHITE + town + " is Captured!", BarColor.GREEN, BarStyle.SOLID);
//                        bar.setVisible(true);
//                        bar.setProgress(1.0);
//                        towndata.getConfig().set("towns." + town + ".progress", 0);
//                        towndata.saveConfig();
//                    } else {
//                        bar = Bukkit.createBossBar(ChatColor.RED + town + "Contested.", BarColor.RED, BarStyle.SOLID);
//                        bar.setVisible(true);
//                        bar.setProgress(1.0);
//                    }
//
//
//                }
//                bar.addPlayer(player);
//                barCreated = true;
//            }
//            if (!isCapturing) { // Not in Capture Zone
//
//
//                this.playerdata.getConfig().set("players." + player.getUniqueId().toString() + ".isCapturing", "false");
//                this.playerdata.saveConfig();
//
//                this.towndata.getConfig().set("towns." + town + ".isCapturing." + player.getUniqueId().toString(), null);
//                this.towndata.saveConfig();
//                this.towndata.reloadConfig();
//                String str = this.towndata.getConfig().getString("towns." + town + ".isCapturing");
//                if (str == null || str.replaceAll(" ", "").isEmpty()) {
//                    Bukkit.getScheduler().cancelTask(capturingEmpires.get(playerEmpire));
//                }
//
//                bar.removePlayer(player);
//                bar.setVisible(false);
//                barCreated = false;
//
//
//            }
//        } catch (Exception e) {
//            player.sendMessage(Arrays.toString(e.getStackTrace()));
//        }
//    }
//
//    public int cast(Player player, String town, String playerEmpire) {
//        this.empiredata = new EmpireDataManager(plugin);
//        this.towndata = new TownDataManager(plugin);
//        double[] progress = {0};
//
//        towndata.reloadConfig();
//        if (this.towndata.getConfig().contains("towns." + town + ".progress")) {
//            progress[0] = Double.parseDouble(this.towndata.getConfig().get("towns." + town + ".progress").toString());
//
//        } else {
//
//            this.towndata.getConfig().set("towns." + town + ".progress", 0);
//            this.towndata.saveConfig();
//
//        }
//
//        return new BukkitRunnable() {
//            double time = 1.0 / (30);
//            boolean dataSaved = false;
//
//            @Override
//            public void run() {
//
//                if (progress[0] >= 1.0) {
//                    if (!dataSaved) {
//                        bar.setTitle(ChatColor.WHITE + town + " is Captured!");
//                        bar.setColor(BarColor.GREEN);
//                        progress[0] = 1.0;
//                        Towns t = new Towns(plugin);
//                        t.townCaptured(player, town);
//                        Bukkit.getScheduler().cancelTask(capturingEmpires.get(playerEmpire));
//                        capturingEmpires.remove(playerEmpire);
//                        dataSaved = true;
//                    }
//
//
//                } else {
//                    progress[0] = progress[0] + time;
//                    towndata.getConfig().set("towns." + town + ".progress", progress[0]);
//                    towndata.saveConfig();
//                    towndata.reloadConfig();
//                    player.sendMessage("After Save -" + towndata.getConfig().getString("towns." + town + ".progress"));
//                }
//
//
//                bar.setProgress(progress[0]);
//
//            }
//        }.runTaskTimer(plugin, 0, 10).getTaskId();
//
//
//    }
//}

package com.tort.EmpireBattles.ProgressBar;


import com.tort.EmpireBattles.Commands.Towns;
import com.tort.EmpireBattles.Files.EmpireDataManager;

import com.tort.EmpireBattles.Files.PlayerDataManager;
import com.tort.EmpireBattles.Files.TownDataManager;
import com.tort.EmpireBattles.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ProgressBar {


    private final Map<String, Integer> capturingEmpires = new ConcurrentHashMap<>();

    //private int taskID;
    private final Main plugin;
    private BossBar bar;
    private EmpireDataManager empiredata;
    private TownDataManager towndata;
    private PlayerDataManager playerdata;


    public ProgressBar(Main plugin) {
        this.plugin = plugin;
    }


    public void addPlayer(Player player) {
        bar.addPlayer(player);
    }

    public boolean barCreated = false;

    public BossBar getBar() {
        return bar;

    }

    public void createBar(Player player, Boolean isCapturing, String town) {
        try {

            player.sendMessage("Attempting to createBar");
            this.playerdata = new PlayerDataManager(plugin);
            this.empiredata = new EmpireDataManager(plugin);
            this.towndata = new TownDataManager(plugin);



            String playerEmpire = playerdata.getConfig().get("players." + player.getUniqueId().toString() + ".empire").toString();
            if (!barCreated && isCapturing) { //If bar is not yet created, create the bar. Set barCreated true in command so multiple bars are not created.

                if (!empiredata.getConfig().contains(playerEmpire + ".towns." + town)) { //If path exists. Won't exist if empire hasn't captured any towns
                    // if town is not captured by empire
                    player.sendTitle(ChatColor.DARK_PURPLE + town, null, 5, 20, 5);
                    bar = Bukkit.createBossBar(ChatColor.DARK_AQUA + "CAPTURING " + town, BarColor.BLUE, BarStyle.SOLID);
                    bar.setVisible(true);
                    if (capturingEmpires.containsKey(playerEmpire)) {
                        player.sendMessage("Empire is already capturing, doing nothing");
                        return;
                    }
                    capturingEmpires.put(playerEmpire, cast(player, town, playerEmpire));
                } else { //If town is already captured by empire

                        bar = Bukkit.createBossBar(ChatColor.WHITE + town + " is Captured!", BarColor.GREEN, BarStyle.SOLID);
                        bar.setVisible(true);
                        bar.setProgress(1.0);
                        towndata.getConfig().set("towns." + town + ".progress", 0);
                        towndata.saveConfig();

                }
                bar.addPlayer(player);
                barCreated = true;
            }
            if (!isCapturing) { // Not in Capture Zone


                this.playerdata.getConfig().set("players." + player.getUniqueId().toString() + ".isCapturing", "false");
                this.playerdata.saveConfig();

                this.towndata.getConfig().set("towns." + town + ".isCapturing." + player.getUniqueId().toString(), null);
                this.towndata.saveConfig();
                this.towndata.reloadConfig();
                String str = this.towndata.getConfig().getString("towns." + town + ".isCapturing");
                if (str == null || str.replaceAll(" ", "").isEmpty()) {
                    player.sendMessage("Cancelling capture task");
                    Bukkit.getScheduler().cancelTask(capturingEmpires.get(playerEmpire));
                    capturingEmpires.remove(playerEmpire);
                }

                bar.removePlayer(player);
                bar.setVisible(false);
                barCreated = false;


            }
        } catch (Exception e) {
            player.sendMessage(Arrays.toString(e.getStackTrace()));
        }
    }

    public int cast(Player player, String town, String playerEmpire) {
        this.empiredata = new EmpireDataManager(plugin);
        this.towndata = new TownDataManager(plugin);
        double[] progress = {0};

        towndata.reloadConfig();
        if (this.towndata.getConfig().contains("towns." + town + ".progress")) {
            progress[0] = Double.parseDouble(this.towndata.getConfig().get("towns." + town + ".progress").toString());

        } else {

            this.towndata.getConfig().set("towns." + town + ".progress", 0);
            this.towndata.saveConfig();

        }

        return new BukkitRunnable() {
            double time = 1.0 / (30);
            boolean dataSaved = false;

            @Override
            public void run() {

                if (progress[0] >= 1.0) {
                    if (!dataSaved) {
                        bar.setTitle(ChatColor.WHITE + town + " is Captured!");
                        bar.setColor(BarColor.GREEN);
                        progress[0] = 1.0;
                        Towns t = new Towns(plugin);
                        t.townCaptured(player, town);
                        Bukkit.getScheduler().cancelTask(capturingEmpires.get(playerEmpire));
                        capturingEmpires.remove(playerEmpire);
                        dataSaved = true;
                    }


                } else {
                    progress[0] = progress[0] + time;
                    towndata.getConfig().set("towns." + town + ".progress", progress[0]);
                    towndata.saveConfig();
                    towndata.reloadConfig();
                    player.sendMessage("After Save -" + towndata.getConfig().getString("towns." + town + ".progress"));
                }


                bar.setProgress(progress[0]);

            }
        }.runTaskTimer(plugin, 0, 10).getTaskId();


    }
}





