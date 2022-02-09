package com.tort.EmpireBattles.EPlayer;

import com.tort.EmpireBattles.Empires.Empire;
import com.tort.EmpireBattles.Main;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

public class EPlayerManager {
    private Main plugin;
    private ArrayList<EPlayer> gameEPlayers = new ArrayList<>();
    private ArrayList<String> gamePlayersUUID = new ArrayList<>();


   public EPlayerManager(Main plugin){
       this.plugin = plugin;
   }

   public EPlayer getEPlayer(Player player){
       for(EPlayer ePlayer : gameEPlayers){
           Player p = ePlayer.getEPlayer();
           if(Objects.equals( p.getUniqueId() , player.getUniqueId())){
               return ePlayer;
           }
       }
       return null;
   }

//    public Player getGamePlayer(Player player){
//        for(Player p : gamePlayers){
//            if(Objects.equals( p , player)){
//                return p;
//            }
//        }
//        return null;
//    }

   public void addEPlayer(EPlayer ePlayer){
       gameEPlayers.add(ePlayer);
   }

    public void addGamePlayerUUID(Player player){
        gamePlayersUUID.add(player.getUniqueId().toString());
    }

   public ArrayList<EPlayer> getGameEPlayers(){
       return gameEPlayers;
   }

   public void removeGameEPlayer(EPlayer ePlayer){
       gameEPlayers.remove(ePlayer);
   }


    public ArrayList<String> getGamePlayersUUID() {
        return gamePlayersUUID;
    }

    public void removeGamePlayer(Player player){
        gameEPlayers.remove(player);
    }






}
