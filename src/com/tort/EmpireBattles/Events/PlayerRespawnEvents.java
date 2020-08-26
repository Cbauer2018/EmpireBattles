package com.tort.EmpireBattles.Events;

import com.tort.EmpireBattles.Files.EmpireDataManager;
import com.tort.EmpireBattles.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class PlayerRespawnEvents implements Listener {
    private Main plugin;

    public PlayerRespawnEvents(Main plugin){
        this.plugin = plugin;

    }


        @EventHandler
        public void onPlayerRespawn(PlayerRespawnEvent event) {
            try {

                Player player = event.getPlayer();
                player.getInventory().clear();
                String team = Main.getTeam(player.getUniqueId().toString());
                ItemStack sword = new ItemStack(Material.STONE_SWORD, 1);
                ItemStack steak = new ItemStack(Material.COOKED_BEEF, 16);
                ItemStack bow = new ItemStack(Material.BOW, 1);
                ItemStack arrows = new ItemStack(Material.ARROW, 32);

                player.getInventory().setItem(0, sword);
                player.getInventory().setItem(1, bow);
                player.getInventory().setItem(2, steak);
                player.getInventory().setItem(9, arrows);

                ItemStack[] armor = new ItemStack[4];
                armor[0] = new ItemStack(Material.LEATHER_BOOTS, 1);
                armor[1] = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                armor[3] = new ItemStack(Material.LEATHER_HELMET, 1);

                LeatherArmorMeta meta0 = (LeatherArmorMeta) armor[0].getItemMeta();
                LeatherArmorMeta meta1 = (LeatherArmorMeta) armor[1].getItemMeta();
                LeatherArmorMeta meta2 = (LeatherArmorMeta) armor[2].getItemMeta();
                LeatherArmorMeta meta3 = (LeatherArmorMeta) armor[3].getItemMeta();
                if (Objects.equals(team, "OTTOMANS")) {

                    meta0.setColor(Color.YELLOW);
                    meta1.setColor(Color.YELLOW);
                    meta2.setColor(Color.YELLOW);
                    meta3.setColor(Color.YELLOW);
                } else if (Objects.equals(team, "MONGOLS")) {

                    meta0.setColor(Color.BLUE);
                    meta1.setColor(Color.BLUE);
                    meta2.setColor(Color.BLUE);
                    meta3.setColor(Color.BLUE);
                } else if (Objects.equals(team, "ROMANS")) {
                    player.setPlayerListName(ChatColor.DARK_RED + "[Roman] " + ChatColor.WHITE + player.getName());
                    meta0.setColor(Color.RED);
                    meta1.setColor(Color.RED);
                    meta2.setColor(Color.RED);
                    meta3.setColor(Color.RED);
                } else {

                    meta0.setColor(Color.PURPLE);
                    meta1.setColor(Color.PURPLE);
                    meta2.setColor(Color.PURPLE);
                    meta3.setColor(Color.PURPLE);
                }
                meta0.setUnbreakable(true);
                meta1.setUnbreakable(true);
                meta2.setUnbreakable(true);
                meta3.setUnbreakable(true);

                armor[0].setItemMeta(meta0);
                armor[1].setItemMeta(meta1);
                armor[2].setItemMeta(meta2);
                armor[3].setItemMeta(meta3);

                player.getInventory().setBoots(armor[0]);
                player.getInventory().setLeggings(armor[1]);
                player.getInventory().setChestplate(armor[2]);
                player.getInventory().setHelmet(armor[3]);
                player.updateInventory();



                Location location = Main.EmpireSpawns.get(team);

                Bukkit.getScheduler().runTaskLater(plugin, () -> player.teleport(location), 5);

            }catch (Exception e){
                getLogger().log(Level.WARNING, Arrays.toString(e.getStackTrace()));
            }

        }


}
