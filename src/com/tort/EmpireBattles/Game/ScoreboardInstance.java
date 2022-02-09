package com.tort.EmpireBattles.Game;

import com.tort.EmpireBattles.Empires.Empire;
import com.tort.EmpireBattles.Files.EmpireUtils;


import com.tort.EmpireBattles.Main;
import com.tort.EmpireBattles.Towns.Town;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class ScoreboardInstance {
    private ScoreboardManager scoreboardManager;
    private Scoreboard board;
    private Objective obj;
   private Map<String,Integer> CapturedTowns = new HashMap<>();
    private ArrayList<Team> teams = new ArrayList<>();

    private Main plugin;

    public ScoreboardInstance(Main plugin){
        this.plugin = plugin;

    }
    public void setScoreBoard(){

       scoreboardManager = Bukkit.getScoreboardManager();
       board = scoreboardManager.getNewScoreboard();
      obj = board.registerNewObjective("Scoreboard-1","dummy","" + ChatColor.DARK_RED + ChatColor.BOLD + "MC" + ChatColor.GOLD + ChatColor.BOLD + " Empires" );
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        ArrayList<Empire> empires = plugin.empireManager.getActiveEmpires();
        for(int i = 0; i < plugin.empireManager.getActiveEmpires().size(); i++){

            Team team = board.registerNewTeam(empires.get(i).getREFERENCE());
            team.setDisplayName(empires.get(i).getREFERENCE());
            team.addEntry(empires.get(i).getEmpireColor() + empires.get(i).getTeamChatPrefix() +  " " + EmpireUtils.firstLetterCap(empires.get(i).getREFERENCE()));
            team.setSuffix("");
            team.setPrefix("");
            teams.add(team);
            CapturedTowns.put(empires.get(i).getREFERENCE(),0);
            obj.getScore(empires.get(i).getEmpireColor() + empires.get(i).getTeamChatPrefix() +  " " + EmpireUtils.firstLetterCap(empires.get(i).getREFERENCE())).setScore(i);

        }

        Team web = board.registerNewTeam("website");
        web.setDisplayName("web");
        web.addEntry("" + ChatColor.GRAY + "www.mcempires.net");

        Team towns = board.registerNewTeam("Towns");
        towns.setDisplayName("towns");
        towns.addEntry("" + ChatColor.AQUA +  "Towns Captured");

        Team blank1 = board.registerNewTeam("blank1");
        blank1 .setDisplayName("blank1");
        blank1 .addEntry("  ");

        Team blank2 = board.registerNewTeam("blank2");
        blank2 .setDisplayName("blank2");
        blank2 .addEntry("   ");

        Team blank3 = board.registerNewTeam("blank3");
        blank3 .setDisplayName("blank3");
        blank3 .addEntry("     ");

    }

    public void updateScoreboard(){
        CapturedTowns.replaceAll((key,value) -> value = 0);



        for(Town town : plugin.townManager.getActiveTowns()) {
            if(CapturedTowns.containsKey(town.getOwner())){
                int i = CapturedTowns.get(town.getOwner());
                i++;
                CapturedTowns.replace(town.getOwner(),i);
            }
        }

        int i = 0;




        obj.getScore("" + ChatColor.GRAY + "www.mcempires.net").setScore(i);
        i++;


        obj.getScore("  ").setScore(i);
        i++;

        Map<String, Integer> sortedMap = EmpireUtils.sortByValue(CapturedTowns);
        for(Map.Entry entry: sortedMap.entrySet()){ //Iterating for dead empires
            Empire empire = plugin.empireManager.getEmpire(entry.getKey().toString());
            if(!empire.getIsAlive()){
                obj.getScore(empire.getEmpireColor() + empire.getTeamChatPrefix() +  " " + EmpireUtils.firstLetterCap(empire.getREFERENCE())).setScore(i);
                i++; // Plus if true
            }
        }

        for(Map.Entry entry: sortedMap.entrySet()){ //Iterating for alive empires. Have to iterate after because of indexing.
            Empire empire = plugin.empireManager.getEmpire(entry.getKey().toString());
            if(empire.getIsAlive()){
                obj.getScore(empire.getEmpireColor() + empire.getTeamChatPrefix() +  " " + EmpireUtils.firstLetterCap(empire.getREFERENCE()) ).setScore(i);
                i++; //Plus if true
            }

        }


        obj.getScore("   ").setScore(i);
        i++;


        obj.getScore("" + ChatColor.AQUA +  "Towns Captured").setScore(i);
        i++;


        obj.getScore("     ").setScore(i);



        for(Team team: teams){
            if(plugin.empireManager.getEmpire(team.getDisplayName()) != null){
                if(plugin.empireManager.getEmpire(team.getDisplayName()).getIsAlive()){
                    team.setPrefix(ChatColor.YELLOW + "♚ ");
                    team.setSuffix(": " + CapturedTowns.get(team.getDisplayName()) + "");
                }else{
                    team.setPrefix(ChatColor.RED + "\uD83D\uDD25 ");
                    team.setSuffix(ChatColor.RED + " \uD83D\uDD25");
                }
            }
        }


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
