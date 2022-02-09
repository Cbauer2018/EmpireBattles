package com.tort.EmpireBattles.Towns;


import com.tort.EmpireBattles.Gates.Gate;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Town {
    private String name;
    private String REFERENCE;
    private String owner;
    private String type;
    private Location TownCaptureZone;
    private Location TownSpawnPoint;
    private Location GateDestroyedSpawn;
    private Gate gate;
    private List<Block> woolBlocks = new ArrayList<>();



    public Town (String REFERENCE){
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
        this.REFERENCE = REFERENCE;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Location getTownCaptureZone() {
        return TownCaptureZone;
    }

    public void setTownCaptureZone(Location townCaptureZone) {
        TownCaptureZone = townCaptureZone;
    }

    public Location getTownSpawnPoint() {
        return TownSpawnPoint;
    }

    public void setTownSpawnPoint(Location townSpawnPoint) {
        TownSpawnPoint = townSpawnPoint;
    }

    public Gate getGate() {
        return gate;
    }

    public void setGate(Gate gate) {
        this.gate = gate;
    }

    public void addWoolBlock(Block block){
        woolBlocks.add(block);
    }

    public void clearBlocks(){
        woolBlocks.clear();
    }

    public List<Block> getWoolBlocks() {
        return woolBlocks;
    }

    public void setWoolBlocks(List<Block> woolBlocks) {
        this.woolBlocks = woolBlocks;
    }
    public void removeBlock(Block block){
        for(Block b : getWoolBlocks()){
            if(Objects.equals(b.getLocation(),block.getLocation())){
                woolBlocks.remove(b);
            }
        }
    }

    public Location getGateDestroyedSpawn() {
        return GateDestroyedSpawn;
    }

    public void setGateDestroyedSpawn(Location gateDestroyedSpawn) {
        GateDestroyedSpawn = gateDestroyedSpawn;
    }
}
