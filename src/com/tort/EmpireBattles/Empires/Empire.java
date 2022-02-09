package com.tort.EmpireBattles.Empires;

import com.tort.EmpireBattles.Gates.Gate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.dynmap.Color;


import java.util.ArrayList;
import java.util.List;

public class Empire {
    private String name;
    private String REFERENCE;
    private ChatColor EmpireColor;
    private String EmpirePrefix;
    private String TeamChatPrefix;
    private ArrayList<Player> EmpirePlayerList = new ArrayList<>();
    private Location EmpireCaptureZone;
    private Location EmpireSpawnPoint;
    private Boolean isAlive;
    private Gate gate;
    private Boolean canCapture;


    public Empire(String REFERENCE){
        this.REFERENCE = REFERENCE.toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getREFERENCE() {
        return REFERENCE;
    }
    public void setREFERENCE(String REFERENCE) {
        this.REFERENCE = REFERENCE.toUpperCase();
    }


    public ChatColor getEmpireColor() {
        return EmpireColor;
    }

    public void setEmpireColor(ChatColor empireColor) {
        EmpireColor = empireColor;
    }

    public String getEmpirePrefix() {
        return EmpirePrefix;
    }

    public void setEmpirePrefix(String empirePrefix) {
        EmpirePrefix = empirePrefix;
    }

    public String getTeamChatPrefix() {
        return TeamChatPrefix;
    }

    public void setTeamChatPrefix(String teamChatPrefix) {
        TeamChatPrefix = teamChatPrefix;
    }

    public ArrayList<Player> getEmpirePlayerList() {
        return EmpirePlayerList;
    }


    public Location getEmpireCaptureZone() {
        return EmpireCaptureZone;
    }

    public void setEmpireCaptureZone(Location empireCaptureZone) {
        EmpireCaptureZone = empireCaptureZone;
    }

    public Location getEmpireSpawnPoint() {
        return EmpireSpawnPoint;
    }

    public void setEmpireSpawnPoint(Location empireSpawnPoint) {
        EmpireSpawnPoint = empireSpawnPoint;
    }

    public Boolean getIsAlive() {
        return isAlive;
    }

    public void setAlive(Boolean alive) {
        isAlive = alive;
    }

    public void removePlayer(Player player){
        if(EmpirePlayerList.contains(player)){
            EmpirePlayerList.remove(player);
        }
    }

    public Gate getGate() {
        return gate;
    }

    public void setGate(Gate gate) {
        this.gate = gate;
    }

    public Boolean getCanCapture() {
        return canCapture;
    }

    public void setCanCapture(Boolean canCapture) {
        this.canCapture = canCapture;
    }
}
