package com.tort.EmpireBattles.Commands;

import com.tort.EmpireBattles.Files.EmpireDataManager;
import com.tort.EmpireBattles.Files.PlayerDataManager;

import com.tort.EmpireBattles.Files.TownDataManager;
import com.tort.EmpireBattles.Main;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class Empires implements CommandExecutor {
    public EmpireDataManager empiredata;
    public PlayerDataManager playerdata;
    private Main plugin;

    public Empires(Main plugin){
        this.plugin = plugin;

    }




    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;
        this.playerdata = new PlayerDataManager(plugin);
        this.empiredata = new EmpireDataManager(plugin);

        if(command.getName().equalsIgnoreCase("empire")){

            if(strings[0].equals("join")){
                Scoreboard board = player.getScoreboard();
                if(Objects.equals(strings[1].toUpperCase(),"OTTOMANS") || Objects.equals(strings[1].toUpperCase(),"MONGOLS") || Objects.equals(strings[1].toUpperCase(),"ROMANS") || Objects.equals(strings[1].toUpperCase(),"VIKINGS")) {
                    try {
                        if(!Objects.equals(Main.getStatus(strings[1].toUpperCase()), true)) {
                            player.sendMessage(String.valueOf(plugin.getStatus(strings[1].toUpperCase())));
                            Team team = board.getTeam(strings[1].toUpperCase());
                            team.addEntry(player.getName());

                            Main.setTeam(player, strings[1].toUpperCase());
                            playerdata.getConfig().set("players." + player.getUniqueId().toString() + ".empire", team.getName());
                            playerdata.saveConfig();

                            empiredata.getConfig().set(team.getName() + ".players." + player.getUniqueId().toString() + ".player_name", player.getDisplayName());
                            empiredata.saveConfig();

                            player.getInventory().clear();
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
                            if (team.getName().equals("OTTOMANS")) {
                                player.setPlayerListName(ChatColor.YELLOW + "[Ottoman] " + ChatColor.WHITE + player.getName());
                                player.setDisplayName(ChatColor.YELLOW + player.getName());
                                meta0.setColor(Color.YELLOW);
                                meta1.setColor(Color.YELLOW);
                                meta2.setColor(Color.YELLOW);
                                meta3.setColor(Color.YELLOW);
                            } else if (team.getName().equals("MONGOLS")) {
                                player.setDisplayName(ChatColor.DARK_BLUE + player.getName());
                                player.setPlayerListName(ChatColor.DARK_BLUE + "[Mongol] " + ChatColor.WHITE + player.getName());
                                meta0.setColor(Color.BLUE);
                                meta1.setColor(Color.BLUE);
                                meta2.setColor(Color.BLUE);
                                meta3.setColor(Color.BLUE);
                            } else if (team.getName().equals("ROMANS")) {
                                player.setDisplayName(ChatColor.DARK_RED + player.getName());
                                player.setPlayerListName(ChatColor.DARK_RED + "[Roman] " + ChatColor.WHITE + player.getName());
                                meta0.setColor(Color.RED);
                                meta1.setColor(Color.RED);
                                meta2.setColor(Color.RED);
                                meta3.setColor(Color.RED);
                            } else {
                                player.setDisplayName(ChatColor.DARK_PURPLE+ player.getName());
                                player.setPlayerListName(ChatColor.DARK_PURPLE + "[Viking] " + ChatColor.WHITE + player.getName());
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

//                    Double X = Double.parseDouble(empiredata.getConfig().get(team.getName() + ".spawnpoint" + ".X").toString());
//                    Double Y = Double.parseDouble(empiredata.getConfig().get(team.getName() + ".spawnpoint" + ".Y").toString());
//                    Double Z = Double.parseDouble(empiredata.getConfig().get(team.getName() + ".spawnpoint" + ".Z").toString());
//                    Float yaw = Float.valueOf(empiredata.getConfig().get(team.getName()+ ".spawnpoint" + ".yaw").toString());
//                    Float pitch = Float.valueOf(empiredata.getConfig().get(team.getName() + ".spawnpoint" + ".pitch").toString());

                            Location location = Main.EmpireSpawns.get(team.getName());
                            player.teleport(location);


                            player.sendMessage("You have joined the " + team.getColor() + team.getName() + ChatColor.WHITE + " !");
                        }else{
                            player.sendMessage(ChatColor.RED + strings[1].toUpperCase() + " have been captured. Select another Empire");
                        }

                    } catch (Exception e) {
                        player.sendMessage("Invalid Empire Name");

                    }
                }

            }else if(strings[0].equals("list")){
                if(playerdata.getConfig().contains("players."+ player.getUniqueId().toString() + ".empire")) {
                    player.sendMessage(empiredata.getConfig().getString(playerdata.getConfig().get("players." + player.getUniqueId().toString() + ".empire").toString() + ".players"));
                }
            }else if(strings[1].equals("setCapture")){

                Main.EmpireZones.put(strings[0].toUpperCase(), player.getLocation().add(0,3,0));
                player.sendMessage(ChatColor.BLUE + "Capture Zone for " + strings[0] + " created!");
            }

        }

        //SET SPAWN COMMAND
        if(command.getName().equalsIgnoreCase("setSpawn")){
            try{
                Main.setStatus(strings[0].toUpperCase(), false);
                empiredata.getConfig().set(strings[0].toUpperCase() + ".spawnpoint" + ".X", player.getLocation().getX());
                empiredata.getConfig().set(strings[0].toUpperCase() + ".spawnpoint" + ".Y", player.getLocation().getY());
                empiredata.getConfig().set(strings[0].toUpperCase() + ".spawnpoint" + ".Z", player.getLocation().getZ());
                empiredata.getConfig().set(strings[0].toUpperCase() + ".spawnpoint" + ".yaw", player.getLocation().getYaw());
                empiredata.getConfig().set(strings[0].toUpperCase() + ".spawnpoint" + ".pitch", player.getLocation().getPitch());
                empiredata.saveConfig();
                player.sendMessage("Spawn point for " + strings[0] + " has been set.");
            }catch (Exception e){
                player.sendMessage("Invalid Empire");
                player.sendMessage(e.toString());
            }

        }


        //SPAWN COMMAND
        if(command.getName().equalsIgnoreCase("spawn")){
            if(!Objects.equals(Main.playerTeams.get(player.getUniqueId().toString()), "NEUTRAL") && Main.playerTeams.containsKey(player.getUniqueId().toString())) {

                String empire = Main.playerTeams.get(player.getUniqueId().toString());

                Double X = Double.parseDouble(empiredata.getConfig().get(empire + ".spawnpoint" + ".X").toString());
                Double Y = Double.parseDouble(empiredata.getConfig().get(empire + ".spawnpoint" + ".Y").toString());
                Double Z = Double.parseDouble(empiredata.getConfig().get(empire + ".spawnpoint" + ".Z").toString());
                Float yaw = Float.valueOf(empiredata.getConfig().get(empire + ".spawnpoint" + ".yaw").toString());
                Float pitch = Float.valueOf(empiredata.getConfig().get(empire + ".spawnpoint" + ".pitch").toString());
                Location location = new Location(player.getWorld(),X,Y,Z,yaw,pitch);
                player.teleport(location);

            }else{
                player.sendMessage(ChatColor.RED + "Join an empire to use /spawn!");
            }
        }
        return true;
    }
}
