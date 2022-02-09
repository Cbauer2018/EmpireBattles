package com.tort.EmpireBattles.Events;

import com.nametagedit.plugin.NametagEdit;
import com.tort.EmpireBattles.EPlayer.EPlayer;
import com.tort.EmpireBattles.Empires.Empire;
import com.tort.EmpireBattles.Files.Colors;
import com.tort.EmpireBattles.Files.EmpireDataManager;
import com.tort.EmpireBattles.Items.*;
import com.tort.EmpireBattles.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
                EPlayer ePlayer = plugin.ePlayerManager.getEPlayer(player);
                Empire playerEmpire = plugin.empireManager.getEmpire(ePlayer.getEPlayerEmpire());

                if(Objects.equals("NEUTRAL",ePlayer.getEPlayerEmpire())){
                    Main.setTeam(player,"NEUTRAL");
                    player.getInventory().clear();
                    event.getPlayer().getEnderChest().clear();
                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.teleport(Bukkit.getServer().getWorld("hubmap").getSpawnLocation()), 5);
                    String prefix = plugin.getPlayerPrefix(player);
                    NametagEdit.getApi().setNametag(event.getPlayer(),prefix + " " + ChatColor.WHITE ,   null  );
                    event.getPlayer().getInventory().setItem(0, EmpireGUI.EmpireGUI);
                    event.getPlayer().getInventory().setItem(8, Guide.Guide);
                    plugin.empireParticlesAPI.setCosmeticItemSlot(event.getPlayer(), 4);
                    player.setPlayerListName(prefix + ChatColor.WHITE + player.getName());
                    return;
                }

                if (Objects.equals(playerEmpire.getIsAlive(), true)) {
                    String prefix = plugin.getPlayerPrefix(player);

                    ItemStack sword = new ItemStack(Material.STONE_SWORD, 1);
                    ItemMeta swordMeta = sword.getItemMeta();
                    swordMeta.setUnbreakable(true);
                    sword.setItemMeta(swordMeta);

                    ItemStack pickaxe = new ItemStack(Material.STONE_PICKAXE, 1);
                    ItemStack steak = new ItemStack(Material.COOKED_BEEF, 8);
                    ItemStack bow = new ItemStack(Material.BOW, 1);
                    ItemMeta bowMeta = bow.getItemMeta();
                    bowMeta.setUnbreakable(true);
                    bow.setItemMeta(bowMeta);
                    ItemStack arrows = new ItemStack(Material.ARROW, 16);

                    player.getInventory().setItem(0, sword);
                    player.getInventory().setItem(1, pickaxe);
                    player.getInventory().setItem(2, bow);
                    player.getInventory().setItem(3, steak);
                    player.getInventory().setItem(7, Cannon.cannon);
                    player.getInventory().setItem(8, townGUI.townGUI);
                    player.getInventory().setItem(9, arrows);
                    event.getPlayer().getInventory().setItem(17, Guide.Guide);
                    plugin.empireParticlesAPI.setCosmeticItemSlot(player,26);

                    ItemStack[] armor = new ItemStack[4];
                    armor[0] = new ItemStack(Material.LEATHER_BOOTS, 1);
                    armor[1] = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                    armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                    armor[3] = new ItemStack(Material.LEATHER_HELMET, 1);

                    LeatherArmorMeta meta0 = (LeatherArmorMeta) armor[0].getItemMeta();
                    LeatherArmorMeta meta1 = (LeatherArmorMeta) armor[1].getItemMeta();
                    LeatherArmorMeta meta2 = (LeatherArmorMeta) armor[2].getItemMeta();
                    LeatherArmorMeta meta3 = (LeatherArmorMeta) armor[3].getItemMeta();

                    ChatColor chatColor = playerEmpire.getEmpireColor();
                    String EmpirePrefix = playerEmpire.getTeamChatPrefix();
                    Color color = Colors.translateChatColorToColor(chatColor);

                    NametagEdit.getApi().setNametag(player,prefix + " " + chatColor, " " + playerEmpire.getTeamChatPrefix());
                    player.setPlayerListName(prefix + chatColor + playerEmpire.getTeamChatPrefix() + ChatColor.WHITE + player.getName());
                    meta0.setColor(color);
                    meta1.setColor(color);
                    meta2.setColor(color);
                    meta3.setColor(color);

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

                    Location location = playerEmpire.getEmpireSpawnPoint();

                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.teleport(location), 5);


                }else{
                    Main.setTeam(player,"NEUTRAL");
                    player.getInventory().clear();
                    event.getPlayer().getEnderChest().clear();
                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.teleport(Bukkit.getServer().getWorld("hubmap").getSpawnLocation()), 5);
                    String prefix = plugin.getPlayerPrefix(player);
                    NametagEdit.getApi().setNametag(event.getPlayer(),prefix + " " + ChatColor.WHITE ,   null  );
                    event.getPlayer().getInventory().setItem(0, EmpireGUI.EmpireGUI);
                    event.getPlayer().getInventory().setItem(8, Guide.Guide);
                    player.setPlayerListName(prefix + ChatColor.WHITE + player.getName());
                }
            }catch (Exception e){
                getLogger().log(Level.WARNING, Arrays.toString(e.getStackTrace()));
            }

        }


}
