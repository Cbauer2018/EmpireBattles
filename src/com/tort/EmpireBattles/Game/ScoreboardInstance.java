package com.tort.EmpireBattles.Game;

import com.tort.EmpireBattles.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Map;
import java.util.Objects;

public class ScoreboardInstance {
    private ScoreboardManager scoreboardManager;
    private Scoreboard board;
    private Objective obj;
    private int OTTOMANS = 0;
    private int MONGOLS = 0;
    private int ROMANS = 0;
    private int VIKINGS = 0;
    private Team ottomanTeam;
    private Team mongolTeam;
    private Team romanTeam;
    private Team vikingTeam;

    private Main plugin;

    public ScoreboardInstance(Main plugin){
        this.plugin = plugin;

    }
    public void setScoreBoard(){
       scoreboardManager = Bukkit.getScoreboardManager();
       board = scoreboardManager.getNewScoreboard();
      obj = board.registerNewObjective("Scoreboard-1","dummy","MCEMPIRES.NET");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        ottomanTeam = board.registerNewTeam("OTTOMANS");
        ottomanTeam.addEntry(ChatColor.YELLOW + "OTTOMAN TOWNS:");
        ottomanTeam.setSuffix("");
        ottomanTeam.setPrefix("");
        obj.getScore(ChatColor.YELLOW + "OTTOMAN TOWNS:").setScore(3);


        mongolTeam = board.registerNewTeam("MONGOLS");
        mongolTeam.addEntry(ChatColor.DARK_BLUE + "MONGOL TOWNS:");
        mongolTeam.setSuffix("");
        mongolTeam.setPrefix("");
        obj.getScore(ChatColor.DARK_BLUE + "MONGOL TOWNS:").setScore(2);


        romanTeam = board.registerNewTeam("ROMANS");
        romanTeam.addEntry(ChatColor.DARK_RED + "ROMAN TOWNS:");
        romanTeam.setSuffix("");
        romanTeam.setPrefix("");
        obj.getScore(ChatColor.DARK_RED + "ROMAN TOWNS:").setScore(1);


        vikingTeam = board.registerNewTeam("VIKINGS");
        vikingTeam.addEntry(ChatColor.DARK_PURPLE + "VIKING TOWNS:");
        vikingTeam.setSuffix("");
        vikingTeam.setPrefix("");
        obj.getScore(ChatColor.DARK_PURPLE+ "VIKING TOWNS:").setScore(0);

    }

    public void updateScoreboard(){
        OTTOMANS = 0;
        MONGOLS = 0;
        ROMANS = 0;
        VIKINGS = 0;

        for(Map.Entry<String, String> entry: plugin.CaptureOwners.entrySet()) {
            if (Objects.equals(entry.getValue(), "OTTOMANS")) {
                OTTOMANS++;
            } else if (Objects.equals(entry.getValue(), "MONGOLS")) {
                MONGOLS++;
            } else if (Objects.equals(entry.getValue(), "ROMANS")) {
                ROMANS++;
            } else if (Objects.equals(entry.getValue(), "VIKINGS")){
                VIKINGS++;
            }
        }

        ottomanTeam.setSuffix(OTTOMANS + "");
        mongolTeam.setSuffix(MONGOLS + "");
        romanTeam.setSuffix(ROMANS + "");
        vikingTeam.setSuffix(VIKINGS + "");




    }


    public void addPlayer(Player player){
        if(player.getScoreboard() != board) {
            player.setScoreboard(board);
        }

    }

    public void removeAllPlayers(){
        board.clearSlot(DisplaySlot.SIDEBAR);
    }


}
