package com.tort.EmpireBattles.Commands;

import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.NametagAPI;
import com.tort.EmpireBattles.EPlayer.EPlayer;
import com.tort.EmpireBattles.Empires.Empire;
import com.tort.EmpireBattles.Empires.EmpireManager;
import com.tort.EmpireBattles.Files.Colors;
import com.tort.EmpireBattles.Files.EmpireDataManager;
import com.tort.EmpireBattles.Files.PlayerDataManager;

import com.tort.EmpireBattles.Game.GameState;
import com.tort.EmpireBattles.Items.Cannon;
import com.tort.EmpireBattles.Items.Cosmetics;
import com.tort.EmpireBattles.Items.Guide;
import com.tort.EmpireBattles.Items.townGUI;
import com.tort.EmpireBattles.Main;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

public class Empires implements CommandExecutor {
    public EmpireDataManager empiredata;

    private Main plugin;
    private ArrayList<Player> OTTOMANS = new ArrayList<>();
    private ArrayList<Player> MONGOLS = new ArrayList<>();
    private ArrayList<Player> ROMANS = new ArrayList<>();
    private ArrayList<Player> VIKINGS = new ArrayList<>();

    public Empires(Main plugin){
        this.plugin = plugin;

    }




    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof  Player)){
            return true;
        }
        Player player = (Player) commandSender;

        this.empiredata = new EmpireDataManager(plugin);

        if(Objects.equals(strings.length,0)){
            player.sendMessage(ChatColor.RED + "To join an empire use /empire join <empire>. EX: /empire join romans");
            return true;
        }

        if(command.getName().equalsIgnoreCase("empire")){ //If command is /empire

            if(strings[0].equals("join")){ // If first argument is join in /empire [command]
                if(strings.length < 2){
                    player.sendMessage(ChatColor.RED + "To join an empire use /empire join <empire>. EX: /empire join romans");
                    return true;
                }
                if(!Objects.equals(strings[1],null)) {
                    if(plugin.gameInstance.getGameState() == GameState.IN_PROCESS) {
                        joinEmpire(player, strings[1].toUpperCase());
                    }else{

                        player.sendMessage(ChatColor.GOLD + "Wait for the game to start!");
                    }
                    return true;// Join empire
                }else{
                    player.sendMessage(ChatColor.RED + "To join an empire use /empire join <empire>. EX: /empire join romans");
                    return true;
                }
            }



            if(strings[0].equals("create")){
                if(!player.hasPermission("empires.admin")){
                    player.sendMessage(ChatColor.RED + "You do not have permission!");
                    return true;
                }
                if(strings.length == 2){
                    Empire empire = new Empire(strings[1]);
                    empire.setAlive(true);
                    plugin.empireManager.addEmpire(empire);
                    player.sendMessage(ChatColor.BLUE + strings[1].toUpperCase() + " has been created!" + ChatColor.GOLD + " " + strings[1].toUpperCase() + ChatColor.BLUE + " is the reference for this empire.");
                    return true;
                }else{
                    player.sendMessage(ChatColor.RED + "Invalid use of command. To create an empire use: /empire create <Empire Reference>");
                    return true;
                }
            }

            if(strings[0].equals("delete")){
                if(!player.hasPermission("empires.admin")){
                    player.sendMessage(ChatColor.RED + "You do not have permission!");
                    return true;
                }
                if(strings.length == 2){
                    plugin.empireManager.removeEmpire(strings[1]);
                    if(this.empiredata.getConfig().contains("Empires." + strings[1].toUpperCase())){
                        this.empiredata.getConfig().set("Empires." + strings[1].toUpperCase(), null);
                        this.empiredata.saveConfig();
                    }

                    player.sendMessage(ChatColor.RED + strings[1].toUpperCase() + " has been removed from empires.");
                    return true;
                }else{
                    player.sendMessage(ChatColor.RED + "Empire not found. Use /empire delete <Empire Reference>");
                    return true;
                }
            }

            if(strings[0].equals("list")){
                if(!player.hasPermission("empires.admin")){
                    player.sendMessage(ChatColor.RED + "You do not have permission!");
                    return true;
                }
                ArrayList<Empire> empires = plugin.empireManager.getActiveEmpires();
                player.sendMessage(ChatColor.GRAY+ "----*" + ChatColor.DARK_RED + "MC" + ChatColor.GOLD + "Empires" + ChatColor.GRAY+ "*----" );
                player.sendMessage(ChatColor.DARK_GRAY + "Empire List: ");
                for(Empire empire : empires){
                    player.sendMessage(ChatColor.GOLD+ empire.getREFERENCE());
                }
                return true;
            }

            //Setters
            if(strings[1].equals("set")){
                if(!player.hasPermission("empires.admin")){
                    player.sendMessage(ChatColor.RED + "You do not have permission!");
                    return true;
                }
                if(Objects.equals(plugin.empireManager.getEmpire(strings[0].toUpperCase()),null)){
                    player.sendMessage(ChatColor.RED + "Invalid Empire Reference.");
                    return true;
                }
                Empire empire = plugin.empireManager.getEmpire(strings[0].toUpperCase());

                if(strings[2].equals("name")){
                   if(strings.length >= 4){
                       StringJoiner joiner = new StringJoiner(" ");
                       for(int i = 3; i < strings.length;i++){
                           joiner.add(strings[i]);
                       }
                       String name = joiner.toString();
                       empire.setName(name);
                       player.sendMessage(ChatColor.BLUE + "Empire " + empire.getREFERENCE() + " name set to:" + ChatColor.GRAY + name);
                   }
                   return true;
                }

                if(strings[2].equals("color")){
                    if(strings.length == 4){
                        ChatColor color = Colors.getChatColorByCode(strings[3]);
                        empire.setEmpireColor(color);
                        player.sendMessage(ChatColor.BLUE + "The color for " + empire.getREFERENCE() + " is set to: " + color + "THIS");
                        player.sendMessage(ChatColor.GRAY + "Info: If the color format is incorrect the default color is white.");
                    }
                    return true;
                }


                if(strings[2].equals("empireprefix")){
                    if(strings.length == 4){
                        String empirePrefix = ChatColor.translateAlternateColorCodes('&', strings[3]);
                        empire.setEmpirePrefix(empirePrefix);
                        player.sendMessage(ChatColor.BLUE + "The empire prefix for " + empire.getREFERENCE() + " is set to: " + empirePrefix);

                    }
                    return true;
                }
                if(strings[2].equals("teamchatprefix")){
                    if(strings.length == 4){
                        String teamChatPrefix = ChatColor.translateAlternateColorCodes('&', strings[3]);
                        empire.setTeamChatPrefix(teamChatPrefix);
                        player.sendMessage(ChatColor.BLUE + "The team chat prefix for " + empire.getREFERENCE() + " is set to: " + teamChatPrefix);

                    }
                    return true;
                }

                if(strings[2].equals("capture")){
                        Location captureLocation = player.getLocation().add(0, 3, 0);
                        empire.setEmpireCaptureZone(captureLocation);
                        player.sendMessage(ChatColor.BLUE + "The capture zone for " + empire.getREFERENCE() + " has been set!");
                    return true;
                }

                if(strings[2].equals("spawn")){
                        Location empireSpawn = new Location(player.getWorld(),player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),player.getLocation().getYaw(),player.getLocation().getPitch());
                       empire.setEmpireSpawnPoint(empireSpawn);
                        player.sendMessage(ChatColor.BLUE + "The spawn for " + empire.getREFERENCE() + " has been set!");

                    return true;
                }
                return true;
            }
            //Getters
            if(strings[1].equals("get")){
                if(!player.hasPermission("empires.admin")){
                    player.sendMessage(ChatColor.RED + "You do not have permission!");
                    return true;
                }
                if(Objects.equals(plugin.empireManager.getEmpire(strings[0]),null)){
                    player.sendMessage(ChatColor.RED + "Invalid Empire Reference.");
                    return true;
                }
                Empire empire = plugin.empireManager.getEmpire(strings[0]);

                if(strings[2].equals("name")){
                    player.sendMessage(ChatColor.GOLD + empire.getREFERENCE() + "'S name is: " + empire.getName());
                    return true;
                }

                if(strings[2].equals("color")){
                    player.sendMessage(ChatColor.GOLD + empire.getREFERENCE() + "'S color is: " + empire.getEmpireColor() + "THIS");
                    return true;
                }

                if(strings[2].equals("empireprefix")){
                    player.sendMessage(ChatColor.GOLD + empire.getREFERENCE() + "'S prefix is: " + empire.getEmpirePrefix());
                    return true;
                }
                if(strings[2].equals("teamprefix")){
                    player.sendMessage(ChatColor.GOLD + empire.getREFERENCE() + "'S team prefix is: " + empire.getTeamChatPrefix());
                    return true;
                }

            }

            if(strings[0] != null){
                player.sendMessage(ChatColor.RED + "To join and empire use /empire join <empire>. EX: /empire join romans");
                return true;
            }

            player.sendMessage(ChatColor.RED + "To join and empire use /empire join <empire>. EX: /empire join romans");
            return true;

            }
        return true;
    }


    public void joinEmpire(Player player, String empire){

        this.empiredata = new EmpireDataManager(plugin);

         Empire playerEmpire = plugin.empireManager.getEmpire(empire.toUpperCase());
        EPlayer ePlayer = plugin.ePlayerManager.getEPlayer(player);

        if(!Objects.equals(playerEmpire,null)) { //If second argument in /empire join [empire] equals an empire
            if(Objects.equals(playerEmpire.getIsAlive(), true)) {

                if(Objects.equals(playerEmpire.getREFERENCE(),ePlayer.getEPlayerEmpire())){
                    player.sendMessage(ChatColor.BLUE + "You are already on this team!");
                    return;
                }

                if(!player.hasPermission("empires.server.balancebypass")) {
                    ArrayList<String> unavailableTeams = unavailableTeams();
                    if (unavailableTeams.contains(empire.toUpperCase())) {
                        player.sendMessage(ChatColor.RED + "Empires are unbalanced please wait for empires to balance or select another empire.");
                        player.sendMessage(ChatColor.GOLD + " Purchase a rank to bypass the balance at " + ChatColor.GREEN + "STORE.MCEMPIRES.NET");
                        return;
                    }
                }
                ePlayer.setEPlayerEmpire(playerEmpire.getREFERENCE());
                removePlayer(player);
                playerEmpire.getEmpirePlayerList().add(player);

        String prefix = plugin.getPlayerPrefix(player);
        player.getEnderChest().clear();
        player.getInventory().clear();

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
        player.getInventory().setItem(8, townGUI.townGUI);
        player.getInventory().setItem(7, Cannon.cannon);
        player.getInventory().setItem(9, arrows);
        player.getInventory().setItem(17, Guide.Guide);
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
        player.setPlayerListName(prefix + chatColor + EmpirePrefix + ChatColor.WHITE + player.getName());
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
        player.teleport(location);
        plugin.getScoreboardInstance().addPlayer(player);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
        player.sendMessage("You have joined " + playerEmpire.getEmpireColor() + playerEmpire.getName() + ChatColor.WHITE + " !");

            }else{
                player.sendMessage(ChatColor.RED + empire.toUpperCase() + " have been captured. Select another Empire"); //notify player empire is captured
            }
        }else{
            player.sendMessage(ChatColor.RED + "Invalid empire name."); //Notify player that the empire name is not valid
        }

    }


  public void removePlayer(Player player){
        ArrayList<Empire> empires = plugin.empireManager.getActiveEmpires();

        for(Empire empire : empires){
            if(empire.getEmpirePlayerList().contains(player)){
                empire.getEmpirePlayerList().remove(player);
            }
        }
  }

  public ArrayList<Player> getPlayerList(String empire){
      if(Objects.equals(empire.toUpperCase(),"OTTOMANS")){
          return OTTOMANS;
      }else if(Objects.equals(empire,"MONGOLS")){
          return MONGOLS;
      }else if(Objects.equals(empire,"ROMANS")){
          return ROMANS;
      }else {
          return VIKINGS;
      }

  }

  public ArrayList<String> unavailableTeams(){
        ArrayList<String> unavailableTeams = new ArrayList<>();
        ArrayList<Empire> empires = plugin.empireManager.getActiveEmpires();
      Map<String,Integer> teams = new HashMap<>();

      for(Empire empire: empires){
          boolean isAlive = empire.getIsAlive();
          if(isAlive)
              teams.put(empire.getREFERENCE(),empire.getEmpirePlayerList().size());
      }

      for(Map.Entry<String, Integer> entry : teams.entrySet()){
          String key=entry.getKey();
          int teamSize=entry.getValue();
          for(Map.Entry<String, Integer> team : teams.entrySet()){
              if(teamSize >= (team.getValue() + 4)){
                    unavailableTeams.add(key);
              }
          }
      }







        return unavailableTeams;
  }





}
