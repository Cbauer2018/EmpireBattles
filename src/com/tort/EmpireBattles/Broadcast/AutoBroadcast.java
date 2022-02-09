package com.tort.EmpireBattles.Broadcast;

import com.tort.EmpireBattles.Files.Colors;
import com.tort.EmpireBattles.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoBroadcast implements  Runnable{
    private Map<Integer, List<String>> broadcasts;
    private int broadcastTask = 0;
    private int count = 0;
    private int size;
    private int requiredPlayers = 0;
    private Main plugin;
    private Sound sound = Sound.BLOCK_NOTE_BLOCK_PLING;
    private double volume = 1.0;
    private double pitch = 1.0;

    public AutoBroadcast(Main plugin, HashMap<Integer,List<String>> broadcasts) {

        this.plugin = plugin;
        this.broadcasts = broadcasts;
        this.size = broadcasts.size();

    }


    @Override
    public void run() {

        if (count == size) count = 0;

        if (count < size && Bukkit.getOnlinePlayers().size() >= requiredPlayers) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                broadcasts.get(count).forEach(message -> {
                    if (message.contains("<center>") && message.contains("</center>"))
                        message = Colors.getCenteredMessage(message);
                    player.sendMessage(Colors.color(message));
                });

                if (sound != null) player.playSound(player.getLocation(), sound, (float) volume, (float) pitch);
            }
            count++;
        }

    }
}
