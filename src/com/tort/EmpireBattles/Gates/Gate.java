package com.tort.EmpireBattles.Gates;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Gate {
    private ArrayList<Sign> gateSigns = new ArrayList<>();
    private String gateOwner;
    private Location gateLocation;
    private Location cannonLocation;
    private String type;
    private boolean isGateDestroyed;
    private boolean isCannonPlaced;
    private boolean hasProgress;
    private boolean newOwner;
    private int CannonHealth;




    public Gate(String gateOwner , String type){
        this.gateOwner = gateOwner;
        this.type = type;
    }

    public ArrayList<Sign> getGateSigns() {
        return gateSigns;
    }

    public void setGateSigns(ArrayList<Sign> gateSigns) {
        this.gateSigns = gateSigns;
    }

    public String getGateOwner() {
        return gateOwner;
    }

    public void setGateOwner(String gateOwner) {
        this.gateOwner = gateOwner;
    }

    public Location getGateLocation() {
        return gateLocation;
    }

    public void setGateLocation(Location gateLocation) {
        this.gateLocation = gateLocation;
    }

    public Location getCannonLocation() {
        return cannonLocation;
    }

    public void setCannonLocation(Location cannonLocation) {
        this.cannonLocation = cannonLocation;
    }

    public boolean isGateDestroyed() {
        return isGateDestroyed;
    }

    public void setGateDestroyed(boolean gateDestroyed) {
        isGateDestroyed = gateDestroyed;
    }

    public boolean isCannonPlaced() {
        return isCannonPlaced;
    }

    public void setCannonPlaced(boolean cannonPlaced) {
        isCannonPlaced = cannonPlaced;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public boolean isHasProgress() {
        return hasProgress;
    }

    public void setHasProgress(boolean hasProgress) {
        this.hasProgress = hasProgress;
    }

    public boolean isNewOwner() {
        return newOwner;
    }

    public void setNewOwner(boolean newOwner) {
        this.newOwner = newOwner;
    }

    public int getCannonHealth() {
        return CannonHealth;
    }

    public void setCannonHealth(int cannonHealth) {
        CannonHealth = cannonHealth;
    }

    public void damageCannon(){
        CannonHealth--;
    }

    public boolean isCannonDead(){
        if(CannonHealth <= 0){
            return true;
        }
        return false;
    }

}
