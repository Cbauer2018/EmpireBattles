package com.tort.EmpireBattles.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class Listeners implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event){
        if(event.getResult() == PlayerLoginEvent.Result.KICK_FULL){
            if(event.getPlayer().hasPermission("empires.server.bypass")){
                event.allow();
            }
        }

    }






}
