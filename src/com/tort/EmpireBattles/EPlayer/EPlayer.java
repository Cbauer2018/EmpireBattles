package com.tort.EmpireBattles.EPlayer;

import org.bukkit.entity.Player;

import java.util.UUID;

public class EPlayer {
    private String EPlayerEmpire;
    private Player player;
    private int TotalKills;
    private int TotalCaptures;
    private int TotalDeaths;
    private int GameKills;
    private int GameCaptures;
    private int GameDeaths;

    public EPlayer(Player player){
        this.player = player;
    }

    public Player getEPlayer() {
        return player;
    }

    public void setEPlayer(Player player) {
        this.player = player;
    }

    public String getEPlayerEmpire() {
        return EPlayerEmpire;
    }

    public void setEPlayerEmpire(String EPlayerEmpire) {
        this.EPlayerEmpire = EPlayerEmpire;
    }

    public int getTotalKills() {
        return TotalKills;
    }

    public void setTotalKills(int totalKills) {
        TotalKills = totalKills;
    }

    public int getTotalCaptures() {
        return TotalCaptures;
    }

    public void setTotalCaptures(int totalCaptures) {
        TotalCaptures = totalCaptures;
    }

    public int getGameKills() {
        return GameKills;
    }

    public void setGameKills(int gameKills) {
        GameKills = gameKills;
    }

    public int getGameCaptures() {
        return GameCaptures;
    }

    public void setGameCaptures(int gameCaptures) {
        GameCaptures = gameCaptures;
    }
    public int getTotalDeaths() {
        return TotalDeaths;
    }

    public void setTotalDeaths(int totalDeaths) {
        TotalDeaths = totalDeaths;
    }

    public int getGameDeaths() {
        return GameDeaths;
    }

    public void setGameDeaths(int gameDeaths) {
        GameDeaths = gameDeaths;
    }
    public void setNewEPlayer(){
        setEPlayerEmpire("NEUTRAL");
        setTotalKills(0);
        setTotalCaptures(0);
        setTotalDeaths(0);
        setGameKills(0);
        setGameCaptures(0);
        setGameDeaths(0);
    }
    public double getGameKD(){
        if(getGameKills() == 0){
            return 0D;
        }
        if(getGameDeaths() == 0){
            return (double) getGameKills();
        }

        double KD = (double) getGameKills()/ (double)getGameDeaths();
        return KD;
    }

    public double getTotalKD(){
        if(getTotalKills() == 0){
            return 0D;
        }
        if(getTotalDeaths() == 0){
            return (double) getTotalKills();
        }

        double KD = (double) getTotalKills()/ (double)getTotalDeaths();
        return KD;
    }

    public void addKill(){
        int totalsKills = getTotalKills();
        int gameKills = getGameKills();
        totalsKills++;
        gameKills++;

        setTotalKills(totalsKills);
        setGameKills(gameKills);
    }

    public void addDeath(){
        int totalDeaths = getTotalDeaths();
        int gameDeaths = getGameDeaths();
        totalDeaths++;
        gameDeaths++;
        setGameDeaths(gameDeaths);
        setTotalDeaths(totalDeaths);
    }
}
