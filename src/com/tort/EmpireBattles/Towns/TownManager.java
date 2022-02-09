package com.tort.EmpireBattles.Towns;

import com.tort.EmpireBattles.Empires.Empire;
import com.tort.EmpireBattles.Main;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TownManager {
    private Main plugin;
    private ArrayList<Town> ActiveTowns = new ArrayList<>();
    private HashMap<Location,String> TownBlocks = new HashMap<>();


    public TownManager(Main plugin){
        this.plugin = plugin;
    }

    public Town getTown(String REFERENCE){
        for(Town town : ActiveTowns){
            String TownReference = town.getREFERENCE();
            if(Objects.equals(TownReference ,REFERENCE.toUpperCase())){
                return town;
            }
        }
        return null;
    }
    public void addTown(Town town){
        ActiveTowns.add(town);
    }

    public ArrayList<Town> getActiveTowns(){
        return ActiveTowns;
    }

    public HashMap<Location,String> getTownBlocks(){
        return TownBlocks;
    }

    public void removeTown(String REFERENCE){
        for(Town town : ActiveTowns){
            String TownReference = town.getREFERENCE();
            if(Objects.equals(TownReference,REFERENCE.toUpperCase())){
                ActiveTowns.remove(town);
                return;
            }
        }
    }

}
