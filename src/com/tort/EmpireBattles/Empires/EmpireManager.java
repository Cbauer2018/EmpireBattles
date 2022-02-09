package com.tort.EmpireBattles.Empires;

import com.tort.EmpireBattles.Main;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Objects;

public class EmpireManager {
    private Main plugin;
    private ArrayList<Empire> ActiveEmpires = new ArrayList<>();

    public EmpireManager(Main plugin){
        this.plugin = plugin;
    }

    public Empire getEmpire(String REFERENCE){
        for(Empire empire : ActiveEmpires){
            String EmpireReference = empire.getREFERENCE();
            if(Objects.equals(EmpireReference,REFERENCE.toUpperCase())){
                return empire;
            }
        }
        return null;
    }
    public void addEmpire(Empire empire){
        ActiveEmpires.add(empire);
    }
    public ArrayList<Empire> getActiveEmpires(){
        return ActiveEmpires;
    }

    public void removeEmpire(String REFERENCE){
        for(Empire empire : ActiveEmpires){
            String EmpireReference = empire.getREFERENCE();
            if(Objects.equals(EmpireReference,REFERENCE.toUpperCase())){
                ActiveEmpires.remove(empire);
                return;
            }
        }
    }
}
