package com.tort.EmpireBattles.Files;

import com.tort.EmpireBattles.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class GateDataManager {

    private Main plugin;
    private FileConfiguration dataConfig = null;
    private File configFile =null;

    public GateDataManager(Main plugin){
        this.plugin = plugin;
        //saves/Initializes config
        saveDefaultConfig();
    }

    public void reloadConfig(){
        if(this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), "gatedata.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = this.plugin.getResource("gatedata.yml");
        if(defaultStream != null){
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig(){
        if(this.dataConfig == null)
            reloadConfig();
        return this.dataConfig;
    }

    public void saveConfig()  {
        if(this.dataConfig == null || this.configFile == null)
            return;
        try {
            this.getConfig().save(this.configFile);
            this.plugin.getLogger().log(Level.INFO,"Data Saved");
        }catch (IOException e){
            this.plugin.getLogger().log(Level.SEVERE,"Could not save config to" + this.configFile, e);
        }
    }

    public void saveDefaultConfig(){
        if(this.configFile == null){
            this.configFile = new File(this.plugin.getDataFolder(), "gatedata.yml");
        }
        if(!this.configFile.exists()){
            this.plugin.saveResource("gatedata.yml",false);
        }
    }

}
