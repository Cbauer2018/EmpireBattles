package com.tort.EmpireBattles.Commands;

import com.tort.EmpireBattles.Files.EmpireDataManager;
import com.tort.EmpireBattles.Files.PlayerDataManager;

import com.tort.EmpireBattles.Files.TownDataManager;
import com.tort.EmpireBattles.Main;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Towns implements CommandExecutor {
    public EmpireDataManager empiredata;
    public TownDataManager towndata;
    public PlayerDataManager playerdata;
    private Main plugin;

    public Towns(Main plugin){
        this.plugin = plugin;

    }
 //Test Comment.
    public void townCaptured(Player player,String town){
        this.empiredata = new EmpireDataManager( plugin);
        this.towndata = new TownDataManager(plugin);
        this.playerdata = new PlayerDataManager(plugin);
        String playerEmpire =playerdata.getConfig().get("players." + player.getUniqueId().toString() + ".empire").toString();
        Material wool;

        if(towndata.getConfig().contains("towns."+ town + ".owner" )) {
            String owner = towndata.getConfig().getString("towns." + town + ".owner");
            empiredata.getConfig().set(owner + ".towns." + town,null);
            empiredata.saveConfig();
        }

        towndata.getConfig().set("towns."+ town + ".owner" , playerEmpire);
        towndata.getConfig().set("towns." + town + ".progress", 0);
        towndata.saveConfig();

        empiredata.getConfig().set(playerEmpire + ".towns." + town,town);
        empiredata.saveConfig();





    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;




        return true;
    }
}
