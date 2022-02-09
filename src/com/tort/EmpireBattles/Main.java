package com.tort.EmpireBattles;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.nametagedit.plugin.NametagEdit;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.tort.EmpireBattles.Broadcast.AutoBroadcast;
import com.tort.EmpireBattles.Commands.Empires;

import com.tort.EmpireBattles.Commands.Gates;
import com.tort.EmpireBattles.Commands.Towns;
import com.tort.EmpireBattles.EPlayer.EPlayer;
import com.tort.EmpireBattles.EPlayer.EPlayerManager;
import com.tort.EmpireBattles.Empires.Empire;
import com.tort.EmpireBattles.Empires.EmpireManager;
import com.tort.EmpireBattles.Events.PlayerRespawnEvents;
import com.tort.EmpireBattles.Explosion.ExplosionManager;
import com.tort.EmpireBattles.Files.*;

import com.tort.EmpireBattles.GUI.GuiManager;
import com.tort.EmpireBattles.Game.*;
import com.tort.EmpireBattles.Gates.Gate;
import com.tort.EmpireBattles.Items.*;


import com.tort.EmpireBattles.NPCS.NPC;
import com.tort.EmpireBattles.NPCS.PacketReaderNPC;
import com.tort.EmpireBattles.NPCS.RenderChecks;
import com.tort.EmpireBattles.NPCS.RightClickNPC;
import com.tort.EmpireBattles.Towns.Town;
import com.tort.EmpireBattles.Towns.TownManager;
import com.tort.EmpireBattles.sql.MySQL;
import com.tort.EmpireBattles.sql.SQLGetter;
import com.tort.EmpireParticles.API.EmpireParticlesAPI;
import com.tort.TortMessages;
import com.tort.messageAPI;
import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.esophose.playerparticles.api.PlayerParticlesAPI;
import dev.esophose.playerparticles.particles.PParticle;
import dev.esophose.playerparticles.particles.ParticleEffect;
import dev.esophose.playerparticles.styles.DefaultStyles;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Observer;
import org.bukkit.block.data.type.Switch;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerIcon;
import org.spigotmc.event.entity.EntityMountEvent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;


public class Main extends JavaPlugin implements Listener {



    private Location blockLocation;
    private ArrayList<Player> inEmpireChat = new ArrayList<>();
    public GameInstance gameInstance;

    public EmpireDataManager empiredata;
    public TownDataManager towndata;
    public GateDataManager gatedata;
    public NPCDataManager npcdata;
    public RenderChecks renderChecks;


    public ScoreboardInstance scoreboardInstance;
    public LuckPerms api;
    public PlayerParticlesAPI ppAPI;
    public DynmapCommonAPI dynmapCommonAPI;
    public GuiManager guiManager;
    public StatHandler statHandler;
    public EmpireManager empireManager;
    public TownManager townManager;
    public EPlayerManager ePlayerManager;
    public ExplosionManager explosionManager = new ExplosionManager(this);


    public EmpireParticlesAPI empireParticlesAPI;
    public messageAPI messageAPI;




    public static Map<String,String> playerTeams = new ConcurrentHashMap<String,String>();
    public static Map<String, Boolean> EmpireStatus = new ConcurrentHashMap<>();
    public Map<UUID,UUID> HorsesAlive = new ConcurrentHashMap<>();
    public Map<String,Long> HorseCooldown = new ConcurrentHashMap<>();
    public Map<UUID,Boolean> playerTeleport = new ConcurrentHashMap<>();

    //For Broadcast
    private HashMap<Integer, List<String>> broadcasts = new HashMap<>();
    private int broadcastTask = 69;



    public static Map<String, Location> CaptureZones = new HashMap<String,Location>();

    public Empires empires;
    public Towns towns;
    public Gates gates;
    public NPC NPC;
    public MySQL SQL;
    public SQLGetter sqlGetter;




    

    @Override
    public void onEnable() {
        //Register Events
        getServer().getPluginManager().registerEvents(new PlayerRespawnEvents(this),this);
        this.getServer().getPluginManager().registerEvents(this,this);
        this.getServer().getPluginManager().registerEvents(explosionManager,this);

        this.saveDefaultConfig();
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
             api = provider.getProvider();

        }

        this.ppAPI = PlayerParticlesAPI.getInstance();
        dynmapCommonAPI = (DynmapCommonAPI) Bukkit.getServer().getPluginManager().getPlugin("dynmap");
        empireParticlesAPI = new EmpireParticlesAPI();
        messageAPI = new messageAPI();


        this.empiredata = new EmpireDataManager(this);
        this.towndata = new TownDataManager(this);
        this.gatedata = new GateDataManager(this);
        this.npcdata = new NPCDataManager(this);


        this.empireManager = new EmpireManager(this);
        this.townManager = new TownManager(this);
        this.ePlayerManager = new EPlayerManager(this);





        NPC = new NPC(this);
        this.renderChecks = new RenderChecks(this, NPC);


        //set Executor for commands

        //Stat Handler
        statHandler = new StatHandler(this);

        //Set Empire Commands

        empires = new Empires(this);
        getCommand("empire").setExecutor(empires);


        //Set Town Commands
        towns = new Towns(this);
        getCommand("town").setExecutor(towns);

        //Set Gate commands
        gates = new Gates(this);
        getCommand("gate").setExecutor(gates);

        //Log enabled Plugin
        getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "[Empire Battles]: Plugin is enabled");

        //Create Game Instance
        gameInstance = new GameInstance(this);

        //Item Init
        CaptureTool.init();
        EmpireGUI.init();
        townGUI.init();
        Cannon.init();
        Guide.init();
        Cosmetics.init();
        NPCSlayer.init();



        //Enable scoreboard
        scoreboardInstance = new ScoreboardInstance(this);
        scoreboardInstance.setScoreBoard();


        //Enable GUI
        guiManager = new GuiManager(this);
        guiManager.createStoreGUI();


        //Restore Configs
        this.restoreCbs();

        //Load SQL
        this.loadMySQL();

        //Hotbar messages


        broadcastTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new AutoBroadcast(this,broadcasts), 60L, 90 * 20);

        for (World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (e instanceof LivingEntity) e.remove();
            }
        }

        WorldCreator c = WorldCreator.name("hubmap");
        c.createWorld();

        if(!Bukkit.getOnlinePlayers().isEmpty()){
            for(Player player : Bukkit.getOnlinePlayers()){
                PacketReaderNPC reader = new PacketReaderNPC(this);
                reader.inject(player);
            }
        }




    }

    @Override
    public void onDisable(){
        this.saveCbs();
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Empire Battles]: Plugin is disabled");
        Bukkit.getScheduler().cancelTask(broadcastTask);

        for(Player player : Bukkit.getOnlinePlayers()){
            PacketReaderNPC reader = new PacketReaderNPC(this);
            reader.uninject(player);

            for(EntityPlayer npc : NPC.getNPCs()){
                NPC.removeNPC(player, npc);
            }
        }
        if(SQL != null){
            SQL.disconnect();
        }



    }


    public void saveCbs(){

        for(Player player: Bukkit.getServer().getOnlinePlayers()){
            EPlayer ePlayer = ePlayerManager.getEPlayer(player);
            sqlGetter.addKills(player.getPlayer().getUniqueId(), ePlayer.getTotalKills());
            sqlGetter.addDeaths(player.getUniqueId(), ePlayer.getTotalDeaths());
            sqlGetter.addCaptures(player.getUniqueId(), ePlayer.getTotalCaptures());
        }

        ArrayList<Empire> empires = empireManager.getActiveEmpires();

        for(Empire empire: empires){
            String name = empire.getName();
            String REFERENCE = empire.getREFERENCE();
            String color = Colors.translateChatColorToString(empire.getEmpireColor());
            String prefix = empire.getEmpirePrefix();
            String teamPrefix = empire.getTeamChatPrefix();
            Location capture = empire.getEmpireCaptureZone();
            Location spawn = empire.getEmpireSpawnPoint();
            Location gateLocation = empire.getGate().getGateLocation();
            Location cannonLocation = empire.getGate().getCannonLocation();
            boolean isAlive = empire.getIsAlive();


            this.empiredata.getConfig().set("Empires." + REFERENCE + ".name",name);
            this.empiredata.getConfig().set("Empires." + REFERENCE + ".color",color.toString());
            this.empiredata.getConfig().set("Empires." + REFERENCE + ".prefix",prefix);
            this.empiredata.getConfig().set("Empires." + REFERENCE + ".teamprefix",teamPrefix);

            this.empiredata.getConfig().set("Empires." + REFERENCE + ".capture" + ".X", capture.getX());
            this.empiredata.getConfig().set("Empires." + REFERENCE + ".capture" + ".Y", capture.getY());
            this.empiredata.getConfig().set("Empires." + REFERENCE + ".capture" + ".Z", capture.getZ());

            this.empiredata.getConfig().set("Empires." + REFERENCE + ".spawn" + ".X", spawn.getX());
            this.empiredata.getConfig().set("Empires." + REFERENCE + ".spawn" + ".Y", spawn.getY());
            this.empiredata.getConfig().set("Empires." + REFERENCE + ".spawn" + ".Z",spawn.getZ());
            this.empiredata.getConfig().set("Empires." + REFERENCE + ".spawn" + ".yaw", spawn.getYaw());
            this.empiredata.getConfig().set("Empires." + REFERENCE + ".spawn" + ".pitch", spawn.getPitch());

            this.empiredata.getConfig().set("Empires." + REFERENCE + ".cannon" + ".X", cannonLocation.getX());
            this.empiredata.getConfig().set("Empires." + REFERENCE + ".cannon" + ".Y", cannonLocation.getY());
            this.empiredata.getConfig().set("Empires." + REFERENCE + ".cannon" + ".Z", cannonLocation.getZ());

            this.empiredata.getConfig().set("Empires." + REFERENCE + ".gate" + ".X", gateLocation.getX());
            this.empiredata.getConfig().set("Empires." + REFERENCE + ".gate" + ".Y", gateLocation.getY());
            this.empiredata.getConfig().set("Empires." + REFERENCE + ".gate" + ".Z", gateLocation.getZ());

            this.empiredata.getConfig().set("Empires." + REFERENCE + ".isalive", isAlive);
            this.empiredata.saveConfig();
        }


        ArrayList<Town> towns = townManager.getActiveTowns();

        for(Town town: towns){
            String name = town.getName();
            String REFERENCE = town.getREFERENCE();
            String owner = town.getOwner();
            String type = town.getType();
            Location capture = town.getTownCaptureZone();
            Location spawn = town.getTownSpawnPoint();
            Location gateLocation = town.getGate().getGateLocation();
            Location cannonLocation = town.getGate().getCannonLocation();
            Location gateDestroyedSpawn = town.getGateDestroyedSpawn();
            List<Block> woolBlocks = town.getWoolBlocks();

            this.towndata.getConfig().set("Towns." + REFERENCE + ".name" , name);
            this.towndata.getConfig().set("Towns." + REFERENCE + ".owner" , owner);
            this.towndata.getConfig().set("Towns." + REFERENCE + ".type" , type);

            this.towndata.getConfig().set("Towns." + REFERENCE + ".capture" + ".X", capture.getX());
            this.towndata.getConfig().set("Towns." + REFERENCE + ".capture" + ".Y", capture.getY());
            this.towndata.getConfig().set("Towns." + REFERENCE + ".capture" + ".Z", capture.getZ());

            this.towndata.getConfig().set("Towns." + REFERENCE + ".spawn" + ".X", spawn.getX());
            this.towndata.getConfig().set("Towns." + REFERENCE + ".spawn" + ".Y", spawn.getY());
            this.towndata.getConfig().set("Towns." + REFERENCE + ".spawn" + ".Z",spawn.getZ());
            this.towndata.getConfig().set("Towns." + REFERENCE + ".spawn" + ".yaw", spawn.getYaw());
            this.towndata.getConfig().set("Towns." + REFERENCE + ".spawn" + ".pitch", spawn.getPitch());

            this.towndata.getConfig().set("Towns." + REFERENCE + ".defend" + ".X", gateDestroyedSpawn.getX());
            this.towndata.getConfig().set("Towns." + REFERENCE + ".defend" + ".Y", gateDestroyedSpawn.getY());
            this.towndata.getConfig().set("Towns." + REFERENCE + ".defend" + ".Z",gateDestroyedSpawn.getZ());
            this.towndata.getConfig().set("Towns." + REFERENCE + ".defend" + ".yaw", gateDestroyedSpawn.getYaw());
            this.towndata.getConfig().set("Towns." + REFERENCE + ".defend" + ".pitch", gateDestroyedSpawn.getPitch());

            this.towndata.getConfig().set("Towns." + REFERENCE + ".cannon" + ".X", cannonLocation.getX());
            this.towndata.getConfig().set("Towns." + REFERENCE + ".cannon" + ".Y", cannonLocation.getY());
            this.towndata.getConfig().set("Towns." + REFERENCE + ".cannon" + ".Z", cannonLocation.getZ());

            this.towndata.getConfig().set("Towns." + REFERENCE + ".gate" + ".X", gateLocation.getX());
            this.towndata.getConfig().set("Towns." + REFERENCE + ".gate" + ".Y", gateLocation.getY());
            this.towndata.getConfig().set("Towns." + REFERENCE + ".gate" + ".Z", gateLocation.getZ());

            for(Block block : woolBlocks){
                double X = block.getX();
                double Y = block.getY();
                double Z = block.getZ();

                int xName = (int) block.getX();
                int yName = (int) block.getY();
                int zName = (int) block.getZ();

                String blockname = String.valueOf(xName) + String.valueOf(yName) + String.valueOf(zName) ;
                this.towndata.getConfig().set("Towns." + REFERENCE + ".blocks." + blockname  + ".X", X);
                this.towndata.getConfig().set("Towns." + REFERENCE + ".blocks."+ blockname  + ".Y", Y);
                this.towndata.getConfig().set("Towns." + REFERENCE + ".blocks."+ blockname  + ".Z", Z);
            }



            this.towndata.saveConfig();
        }

        this.reloadConfig();
        if(townManager.getTownBlocks().size() > 0) {
            for (Map.Entry<Location, String> entry : townManager.getTownBlocks().entrySet()) {
                double X = entry.getKey().getX();
                double Y = entry.getKey().getY();
                double Z = entry.getKey().getZ();

                int xname = (int) entry.getKey().getX();  // the "." messes up the config file so a integer must be used
                int yname = (int) entry.getKey().getY();
                int zname = (int) entry.getKey().getZ();
                String blockname = String.valueOf(xname) + String.valueOf(yname) + String.valueOf(zname);
                this.getConfig().set("blocks." + blockname + ".X", X);
                this.getConfig().set("blocks." + blockname + ".Y", Y);
                this.getConfig().set("blocks." + blockname + ".Z", Z);
                this.getConfig().set("blocks." + blockname + ".town", entry.getValue());
            }
        }

        this.saveConfig();
    }

    // Cbs = Capture Blocks
    public void restoreCbs(){

        Collection<String> Empires = this.empiredata.getConfig().getConfigurationSection("Empires.").getKeys(false);
        for(String REFERENCE : Empires){
           String name = this.empiredata.getConfig().getString("Empires." + REFERENCE + ".name");
           String color = this.empiredata.getConfig().getString("Empires." + REFERENCE + ".color");
           String prefix = this.empiredata.getConfig().getString("Empires." + REFERENCE + ".prefix");
           String teamPrefix = this.empiredata.getConfig().getString("Empires." + REFERENCE + ".teamprefix");

           Double captureX = this.empiredata.getConfig().getDouble("Empires." + REFERENCE + ".capture" + ".X");
           Double captureY = this.empiredata.getConfig().getDouble("Empires." + REFERENCE + ".capture" + ".Y");
           Double captureZ = this.empiredata.getConfig().getDouble("Empires." + REFERENCE + ".capture" + ".Z");

            Double spawnX = this.empiredata.getConfig().getDouble("Empires." + REFERENCE + ".spawn" + ".X");
            Double spawnY = this.empiredata.getConfig().getDouble("Empires." + REFERENCE + ".spawn" + ".Y");
            Double spawnZ = this.empiredata.getConfig().getDouble("Empires." + REFERENCE + ".spawn" + ".Z");
            Float yaw = Float.valueOf(this.empiredata.getConfig().getString("Empires." + REFERENCE + ".spawn" + ".yaw"));
            Float pitch = Float.valueOf(this.empiredata.getConfig().getString("Empires." + REFERENCE + ".spawn" + ".pitch"));

            Double gateX = this.empiredata.getConfig().getDouble("Empires." + REFERENCE + ".gate" + ".X");
            Double gateY = this.empiredata.getConfig().getDouble("Empires." + REFERENCE + ".gate" + ".Y");
            Double gateZ = this.empiredata.getConfig().getDouble("Empires." + REFERENCE + ".gate" + ".Z");

            Double cannonX = this.empiredata.getConfig().getDouble("Empires." + REFERENCE + ".cannon" + ".X");
            Double cannonY = this.empiredata.getConfig().getDouble("Empires." + REFERENCE + ".cannon" + ".Y");
            Double cannonZ = this.empiredata.getConfig().getDouble("Empires." + REFERENCE + ".cannon" + ".Z");

           boolean isAlive = this.empiredata.getConfig().getBoolean("Empires." + REFERENCE + ".isalive");

           Empire empire = new Empire(REFERENCE);
           empire.setName(name);
           empire.setEmpireColor(Colors.translateStringtoChatColor(color));
           empire.setEmpirePrefix(prefix);
           empire.setTeamChatPrefix(teamPrefix);

           Location capLocation = new Location(Bukkit.getServer().getWorld("world"),captureX,captureY,captureZ);
           empire.setEmpireCaptureZone(capLocation);

           Location spawnLocation = new Location(Bukkit.getServer().getWorld("world"),spawnX,spawnY,spawnZ,yaw,pitch);
           empire.setEmpireSpawnPoint(spawnLocation);


            Gate gate = new Gate(REFERENCE,"empire");

           Location gateLocation = new Location(Bukkit.getServer().getWorld("world"), gateX,gateY,gateZ);
           gate.setGateLocation(gateLocation);

           Location cannonLocation = new Location(Bukkit.getServer().getWorld("world"), cannonX,cannonY,cannonZ);
           gate.setCannonLocation(cannonLocation);
           empire.setGate(gate);

           empire.setAlive(isAlive);
           empireManager.addEmpire(empire);

        }

        Collection<String> Towns = this.towndata.getConfig().getConfigurationSection("Towns.").getKeys(false);
        for(String REFERENCE : Towns){
            String name = this.towndata.getConfig().getString("Towns." + REFERENCE + ".name");
            String owner = this.towndata.getConfig().getString("Towns." + REFERENCE + ".owner");
            String type = this.towndata.getConfig().getString("Towns." + REFERENCE + ".type");

            Double captureX = this.towndata.getConfig().getDouble("Towns." + REFERENCE + ".capture" + ".X");
            Double captureY = this.towndata.getConfig().getDouble("Towns." + REFERENCE + ".capture" + ".Y");
            Double captureZ = this.towndata.getConfig().getDouble("Towns." + REFERENCE + ".capture" + ".Z");

            Double spawnX = this.towndata.getConfig().getDouble("Towns." + REFERENCE + ".spawn" + ".X");
            Double spawnY = this.towndata.getConfig().getDouble("Towns." + REFERENCE + ".spawn" + ".Y");
            Double spawnZ = this.towndata.getConfig().getDouble("Towns." + REFERENCE + ".spawn" + ".Z");
            Float yaw = Float.valueOf(this.towndata.getConfig().getString("Towns." + REFERENCE + ".spawn" + ".yaw"));
            Float pitch = Float.valueOf(this.towndata.getConfig().getString("Towns." + REFERENCE + ".spawn" + ".pitch"));





            Double gateX = this.towndata.getConfig().getDouble("Towns." + REFERENCE + ".gate" + ".X");
            Double gateY = this.towndata.getConfig().getDouble("Towns." + REFERENCE + ".gate" + ".Y");
            Double gateZ = this.towndata.getConfig().getDouble("Towns." + REFERENCE + ".gate" + ".Z");

            Double cannonX = this.towndata.getConfig().getDouble("Towns." + REFERENCE + ".cannon" + ".X");
            Double cannonY = this.towndata.getConfig().getDouble("Towns." + REFERENCE + ".cannon" + ".Y");
            Double cannonZ = this.towndata.getConfig().getDouble("Towns." + REFERENCE + ".cannon" + ".Z");


            Town town = new Town(REFERENCE);
            town.setName(name);
            town.setOwner(owner);
            town.setType(type);

            Location capLocation = new Location(Bukkit.getServer().getWorld("world"),captureX,captureY,captureZ);
            town.setTownCaptureZone(capLocation);

            Location spawnLocation = new Location(Bukkit.getServer().getWorld("world"),spawnX,spawnY,spawnZ,yaw,pitch);
            town.setTownSpawnPoint(spawnLocation);

            if(this.towndata.getConfig().get("Towns." + REFERENCE + ".defend") != null){
                Double defendX = this.towndata.getConfig().getDouble("Towns." + REFERENCE + ".defend" + ".X");
                Double defendY = this.towndata.getConfig().getDouble("Towns." + REFERENCE + ".defend" + ".Y");
                Double defendZ = this.towndata.getConfig().getDouble("Towns." + REFERENCE + ".defend" + ".Z");
                Float defendYaw = Float.valueOf(this.towndata.getConfig().getString("Towns." + REFERENCE + ".defend" + ".yaw"));
                Float defendPitch = Float.valueOf(this.towndata.getConfig().getString("Towns." + REFERENCE + ".defend" + ".pitch"));
                Location gateDestroyedLocation = new Location(Bukkit.getServer().getWorld("world"),defendX,defendY,defendZ,defendYaw,defendPitch);
                town.setGateDestroyedSpawn(gateDestroyedLocation);

            }

            Gate gate = new Gate(REFERENCE,"town");

            Location gateLocation = new Location(Bukkit.getServer().getWorld("world"), gateX,gateY,gateZ);
            gate.setGateLocation(gateLocation);

            Location cannonLocation = new Location(Bukkit.getServer().getWorld("world"), cannonX,cannonY,cannonZ);
            gate.setCannonLocation(cannonLocation);
            town.setGate(gate);
            if(this.towndata.getConfig().getConfigurationSection("Towns."+ REFERENCE + ".blocks.") != null) {
                Collection<String> blocks = this.towndata.getConfig().getConfigurationSection("Towns." + REFERENCE + ".blocks").getKeys(false);

            for(String block : blocks){

                double X = this.towndata.getConfig().getDouble("Towns."+ REFERENCE + ".blocks." + block + ".X");
                double Y = this.towndata.getConfig().getDouble("Towns."+ REFERENCE + ".blocks." + block + ".Y");
                double Z = this.towndata.getConfig().getDouble("Towns."+ REFERENCE + ".blocks." + block + ".Z");

                Location blockLocation = new Location(getServer().getWorld("world"),X,Y,Z);
                Block b = blockLocation.getBlock();
                town.addWoolBlock(b);
            }
            }
            townManager.addTown(town);

        }

        if(this.getConfig().getConfigurationSection("blocks.") != null) {
            Collection<String> blocks = this.getConfig().getConfigurationSection("blocks.").getKeys(false);
            for (String block : blocks) {

                double X = this.getConfig().getDouble("blocks." + block + ".X");
                double Y = this.getConfig().getDouble("blocks." + block + ".Y");
                double Z = this.getConfig().getDouble("blocks." + block + ".Z");
                String town = this.getConfig().getString("blocks." + block + ".town");

                Location blockLocation = new Location(getServer().getWorld("world"), X, Y, Z);
                townManager.getTownBlocks().put(blockLocation, town);
            }

        }
        int count = 0;
        if(this.getConfig().getConfigurationSection("broadcasts.") != null) {
            for (String key : this.getConfig().getConfigurationSection("broadcasts.").getKeys(false)) {
                this.broadcasts.put(count,getConfig().getStringList("broadcasts." + key));
                count++;
            }
        }


        FileConfiguration file = npcdata.getConfig();
        npcdata.getConfig().getConfigurationSection("data").getKeys(false).forEach(npc ->{
            Location location = new Location(Bukkit.getWorld(file.getString("data." + npc + ".world")),
                    file.getInt("data." + npc + ".x"),
                    file.getInt("data." + npc + ".y"),
                    file.getInt("data." + npc + ".z"),
                    (float) file.getDouble("data." + npc + ".yaw"),
                    (float) file.getDouble("data." + npc + ".pitch")
                    );

            String name = file.getString("data." + npc + ".name");;
            GameProfile gameProfile = new GameProfile(UUID.fromString( file.getString("data." + npc + ".uuid")), ChatColor.GREEN + "" + ChatColor.BOLD + name);
            gameProfile.getProperties().put("textures", new Property("textures", file.getString("data." + npc + ".texture"),
                    file.getString("data." + npc + ".signature")));

            NPC.loadNPC(location,gameProfile, this);
        });

    }



    public void loadMySQL(){
        if(this.getConfig().getConfigurationSection("sql.") != null) {
            String host = this.getConfig().getString("sql." + "address");
            String database = this.getConfig().getString("sql." + "database");
            String username = this.getConfig().getString("sql." + "username");
            String password = this.getConfig().getString("sql." + "password");


            this.SQL = new MySQL(host,database,username,password);
            this.sqlGetter = new SQLGetter(this);


            try {
                SQL.connect();
            } catch (ClassNotFoundException e) {

                Bukkit.getLogger().info("Database not connected.");
                //e.printStackTrace();
            } catch (SQLException throwables) {
                Bukkit.getLogger().info("Database not connected.");
                //throwables.printStackTrace();
            }

            if(SQL.isConnected()){
                Bukkit.getLogger().info("Database is connected.");
                sqlGetter.createTable();
            }

        }
    }


    /*

        -------------------------------------------------------------------------------





       FUNCTIONS





        -------------------------------------------------------------------------------

     */

    public static String getWinner(){
        for(Map.Entry<String,Boolean> entry: EmpireStatus.entrySet()){
            if(!entry.getValue()){
                return entry.getKey();
            }
        }
        return "null";
    }

    public boolean isInCombat(Player player) {
        // Make sure to check that CombatLogX is enabled before using it for anything.
        ICombatLogX plugin = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
        ICombatManager combatManager = plugin.getCombatManager();
        return combatManager.isInCombat(player);
    }



    public static void setTeam(Player player, String empire){
        if(Main.playerTeams.containsKey(player.getUniqueId().toString())) {
            Main.playerTeams.replace(player.getUniqueId().toString(), empire.toUpperCase());
        }else{
            Main.playerTeams.put(player.getUniqueId().toString(), empire.toUpperCase());
        }
    }

    public void changeTownColor(String town , String owner){

        ChatColor chatColor;
        if(Objects.equals("NEUTRAL",owner)){
            chatColor = ChatColor.WHITE;
        }else{
            chatColor = empireManager.getEmpire(owner).getEmpireColor();
        }
        Town t = townManager.getTown(town);
        int color = Colors.translateChatColorToDynmapColor(chatColor);
        Collection<AreaMarker> set = dynmapCommonAPI.getMarkerAPI().getMarkerSet("markers").getAreaMarkers();
        for(AreaMarker areaMarker : set){
            String l = areaMarker.getLabel();
            if(Objects.equals(l.toUpperCase(),t.getREFERENCE())){
                areaMarker.setLineStyle(3, 1.0, color);
                areaMarker.setFillStyle(0.35,color);
                areaMarker.setGreetingText(chatColor + t.getName(), t.getType()+ " TOWN");

            }
        }

    }

    public void setEmpireDynmapColor(Empire empire){

        ChatColor chatColor = empire.getEmpireColor();

        int color = Colors.translateChatColorToDynmapColor(chatColor);
        Collection<AreaMarker> set = dynmapCommonAPI.getMarkerAPI().getMarkerSet("markers").getAreaMarkers();
        for(AreaMarker areaMarker : set){
            String l = areaMarker.getLabel();
            if(Objects.equals(l.toUpperCase(),empire.getREFERENCE())){
                areaMarker.setLineStyle(3, 1.0, color);
                areaMarker.setFillStyle(0.35,color);
                areaMarker.setGreetingText(chatColor + empire.getName(), null);

            }
        }

    }

    public void setDynmapIconFire(Empire empire){

        Collection<Marker> set = dynmapCommonAPI.getMarkerAPI().getMarkerSet("markers").getMarkers();
        for(Marker marker : set){
            String l = marker.getLabel();
            if(Objects.equals(l.toUpperCase(),empire.getREFERENCE())){
                marker.setMarkerIcon(dynmapCommonAPI.getMarkerAPI().getMarkerIcon("fire"));

            }
        }

    }

    public void resetIcon(Empire empire){

        Collection<Marker> set = dynmapCommonAPI.getMarkerAPI().getMarkerSet("markers").getMarkers();
        for(Marker marker : set){
            String l = marker.getLabel();
            if(Objects.equals(l.toUpperCase(),empire.getREFERENCE())){
                marker.setMarkerIcon(dynmapCommonAPI.getMarkerAPI().getMarkerIcon("king"));

            }
        }

    }

    public boolean areEnemies(Player a , Player b){
        String aTeam = ePlayerManager.getEPlayer(a).getEPlayerEmpire();
        String bTeam = ePlayerManager.getEPlayer(b).getEPlayerEmpire();
        if(Objects.equals(aTeam,bTeam)){
            return false;
        }
        if(Objects.equals(aTeam,"NEUTRAL") || Objects.equals(bTeam,"NEUTRAL") ){
            return false;
        }
        return true;
    }

    public String getPlayerPrefix(Player player){
        User user =  api.getUserManager().getUser(player.getUniqueId());
        QueryOptions queryOptions = api.getContextManager().getQueryOptions(player);
        CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);
        String setPrefix = metaData.getPrefix();
        String prefix = "";
        if(setPrefix != null) {
            prefix = ChatColor.translateAlternateColorCodes('&', setPrefix);
            return prefix;
        }
        return prefix;

    }

    public String getPlayerGroup(Player player){
        User user =  api.getUserManager().getUser(player.getUniqueId());
        QueryOptions queryOptions = api.getContextManager().getQueryOptions(player);
        CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);
        String group = metaData.getPrimaryGroup();
        return group;

    }
    public void warpTown(Player player , Town town){
        int taskID = -1;
        if(townManager.getActiveTowns().contains(town)){
            if(Objects.equals(ePlayerManager.getEPlayer(player).getEPlayerEmpire(), town.getOwner())) {
                if(!playerTeleport.containsKey(player.getUniqueId())) {
                    Random random = new Random();
                    double rand = random.nextInt(6);
                    Location loca = town.getTownSpawnPoint();
                    Location locb = new Location(loca.getWorld(), loca.getX(), loca.getY(), loca.getZ() , loca.getYaw() , loca.getPitch());
                    locb.add(rand, 0, rand);

                    Location locD = town.getGateDestroyedSpawn();
                    Location locDefend = new Location(locD.getWorld(), locD.getX(), locD.getY(), locD.getZ(), locD.getYaw(), locD.getPitch());
                    locDefend.add(rand, 0, rand);

                    Location spawnLoc;
                    int delay; //delay in seconds
                    if(town.getGate().isGateDestroyed()){
                        spawnLoc = locDefend;
                        delay = 10;
                        player.sendMessage(ChatColor.GOLD + "The gate is destroyed! Teleporting to " + town.getName() + " in " + delay + " seconds. Don't move!");
                    }else{
                        spawnLoc = locb;
                        delay = 10;
                        player.sendMessage(ChatColor.GOLD + "Teleporting to " + town.getName() + " in " + delay + " seconds. Don't move!");
                    }


                    playerTeleport.put(player.getUniqueId(), false); //map storing their id to whether they moved



                    taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            if (!playerTeleport.get(player.getUniqueId())) { //if thy havnt moved
                                player.teleport(spawnLoc);// teleports them to the location
                                playerTeleport.remove(player.getUniqueId());// remove them from the map
                            } else {
                                player.sendMessage(ChatColor.RED + "You moved!");
                                playerTeleport.remove(player.getUniqueId());// remove them from the map
                            }
                        }
                    }, 20 * delay);


                    int finalTaskID = taskID;
                    BukkitRunnable bukkitRunnable = new BukkitRunnable() {
                        int timer = 0;
                        @Override
                        public void run() {
                            if (playerTeleport.get(player.getUniqueId())) { //if player moved
                                player.sendMessage(ChatColor.RED + "You moved!");
                                playerTeleport.remove(player.getUniqueId());// remove them from the map
                                Bukkit.getServer().getScheduler().cancelTask(finalTaskID);
                                this.cancel();
                            } else{
                                timer++;
                                Bukkit.getServer().getWorld("world").spawnParticle(Particle.END_ROD,player.getLocation().add(0,4,0),0);
                                if(timer == delay){
                                    this.cancel();
                                }
                            }

                        }
                    };
                    bukkitRunnable.runTaskTimer(this, 0, 20);



                }else{
                    player.sendMessage(ChatColor.RED + "You are already teleporting!");
                }
            }else{
                player.sendMessage(ChatColor.RED + " Your team does not own this town.");
            }
        }else{
            player.sendMessage(ChatColor.RED + " Invalid town name.");
        }
    }

    public void warpCapital(Player player){
        if(!Objects.equals(ePlayerManager.getEPlayer(player).getEPlayerEmpire(), "NEUTRAL")) { // If player isnt neutral

            Empire empire = empireManager.getEmpire(ePlayerManager.getEPlayer(player).getEPlayerEmpire());//empire of team
            Location location = empire.getEmpireSpawnPoint(); // Empire's spawn
            if(!playerTeleport.containsKey(player.getUniqueId())) {

                int delay = 10; //delay in seconds
                int taskID = -1;
                player.sendMessage(ChatColor.GOLD + "Teleporting to the capital in 10 seconds. Don't move!");
                playerTeleport.put(player.getUniqueId(), false); //map storing their id to whether they moved

                taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        if (!playerTeleport.get(player.getUniqueId())) { //if thy havnt moved
                            player.teleport(location);// teleports them to the location
                            playerTeleport.remove(player.getUniqueId());// remove them from the map
                        } else {
                            player.sendMessage(ChatColor.RED + "You moved!");
                            playerTeleport.remove(player.getUniqueId());// remove them from the map
                        }
                    }
                }, 20 * delay);


                int finalTaskID = taskID;
                BukkitRunnable bukkitRunnable = new BukkitRunnable() { //for particles and checking movement
                    int timer = 0;
                    @Override
                    public void run() {
                        if (playerTeleport.get(player.getUniqueId())) { //if player moved
                            player.sendMessage(ChatColor.RED + "You moved!");
                            playerTeleport.remove(player.getUniqueId());// remove them from the map
                            Bukkit.getServer().getScheduler().cancelTask(finalTaskID);
                            this.cancel();
                        } else{
                            timer++;
                            Bukkit.getServer().getWorld("world").spawnParticle(Particle.END_ROD,player.getLocation().add(0,4,0),0);
                            if(timer == delay){
                                this.cancel();
                            }
                        }

                    }
                };
                bukkitRunnable.runTaskTimer(this, 0, 20);
            }else {
                player.sendMessage(ChatColor.RED + "You are already teleporting!");
            }

        }else{
            player.sendMessage(ChatColor.RED + "Join an empire to use /spawn!");
        }

    }

    public void resetGame(){
        gameInstance.stop();

        for (Empire empire: empireManager.getActiveEmpires()) {
            empire.getEmpirePlayerList().clear();
            empire.setAlive(true);
            resetIcon(empire);
        }
        for(Town town: townManager.getActiveTowns()){
            town.setOwner("NEUTRAL");
            for(Block block: town.getWoolBlocks()){
                block.setType(Material.WHITE_WOOL);
            }
            changeTownColor(town.getREFERENCE(),"NEUTRAL");
        }


        ePlayerManager.getGameEPlayers().clear();
        ePlayerManager.getGamePlayersUUID().clear();

        for(Player p: Bukkit.getOnlinePlayers()){
            EPlayer ePlayer = new EPlayer(p);

            ePlayer.setTotalKills(sqlGetter.getKills(p.getUniqueId()));
            ePlayer.setTotalCaptures(sqlGetter.getCaptures(p.getUniqueId()));
            ePlayer.setTotalDeaths(sqlGetter.getDeaths(p.getUniqueId()));
            ePlayer.setGameKills(0);
            ePlayer.setGameCaptures(0);
            ePlayer.setGameDeaths(0);
            ePlayer.setEPlayerEmpire("NEUTRAL");

            ePlayerManager.addEPlayer(ePlayer);
            ePlayerManager.addGamePlayerUUID(p);
            String prefix = getPlayerPrefix(p);
            p.getInventory().clear();
            p.getEnderChest().clear();
            p.teleport(Bukkit.getServer().getWorld("hubmap").getSpawnLocation());
            p.setPlayerListName(prefix + ChatColor.WHITE + p.getName());
            NametagEdit.getApi().setNametag(p,prefix + " " + ChatColor.WHITE ,   null  );
            p.getInventory().setItem(0,EmpireGUI.EmpireGUI);
            p.getInventory().setItem(8,Guide.Guide);
            p.getInventory().setItem(4, Cosmetics.Cosmetics);
        }
        if(!Objects.equals(gameInstance.getGameScoreboard(), null)) {
            gameInstance.getGameScoreboard().removeAllPlayers();
        }



        ArrayList<CapZone> capZoneArraylist = gameInstance.getCapZones();

        for(CapZone zone : capZoneArraylist){
            zone.setZoneOwner("NEUTRAL");
        }


    }

    public Location getBlockLocation() {
        return blockLocation;
    }

    public NPC getMainNPC(){return NPC;}

    public void addPackets(Player player){
        if(!Objects.equals(NPC.getNPCs(),null)  && !NPC.getNPCs().isEmpty() ){
            NPC.addJoinPacket(player, this);
        }
    }

    public ScoreboardInstance getScoreboardInstance(){return  gameInstance.getGameScoreboard();}


/*

        -------------------------------------------------------------------------------





        EVENTS





        -------------------------------------------------------------------------------

     */

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        String prefix = getPlayerPrefix(event.getPlayer());

        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                //This is where you should do your database interaction
                sqlGetter.createPlayer(player);
            }
        };
        r.runTaskAsynchronously(this);

        if(!Objects.equals(NPC.getNPCs(),null)  && !NPC.getNPCs().isEmpty() ){
            Bukkit.getScheduler().runTaskLater(this, ()->NPC.addJoinPacket(player, this),100);
            PacketReaderNPC reader = new PacketReaderNPC(this);
            reader.inject(player);
        }


        if(!ePlayerManager.getGamePlayersUUID().contains(player.getUniqueId().toString())){

            EPlayer ePlayer = new EPlayer(event.getPlayer());

            ePlayer.setTotalKills(sqlGetter.getKills(player.getUniqueId()));
            ePlayer.setTotalCaptures(sqlGetter.getCaptures(player.getUniqueId()));
            ePlayer.setTotalDeaths(sqlGetter.getDeaths(player.getUniqueId()));
            ePlayer.setGameKills(0);
            ePlayer.setGameCaptures(0);
            ePlayer.setGameDeaths(0);
            ePlayer.setEPlayerEmpire("NEUTRAL");


            ePlayerManager.addEPlayer(ePlayer);
            ePlayerManager.addGamePlayerUUID(player);

            event.getPlayer().getInventory().clear();
            event.getPlayer().getEnderChest().clear();
            Bukkit.getScheduler().runTaskLater(this, () -> player.teleport(Bukkit.getServer().getWorld("hubmap").getSpawnLocation()), 5);
            event.getPlayer().setPlayerListName(prefix + ChatColor.WHITE + event.getPlayer().getName());
            event.setJoinMessage(prefix + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the game.");
            NametagEdit.getApi().setNametag(event.getPlayer(),prefix + " " + ChatColor.WHITE ,   null  );
            event.getPlayer().getInventory().setItem(0,EmpireGUI.EmpireGUI);
            event.getPlayer().getInventory().setItem(8,Guide.Guide);
            empireParticlesAPI.setCosmeticItemSlot(event.getPlayer(), 4);
            return;
        }else{

            EPlayer ePlayer = ePlayerManager.getEPlayer(player);
            if(Objects.equals(ePlayer.getEPlayerEmpire(),"NEUTRAL")){ //If Restart of game.
                event.getPlayer().getInventory().clear();
                Bukkit.getScheduler().runTaskLater(this, () -> player.teleport(Bukkit.getServer().getWorld("hubmap").getSpawnLocation()), 5);
                event.getPlayer().setPlayerListName(prefix + ChatColor.WHITE + event.getPlayer().getName());
                event.setJoinMessage(prefix + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the game.");
                NametagEdit.getApi().setNametag(event.getPlayer(),prefix + " " + ChatColor.WHITE ,   null  );
                event.getPlayer().getInventory().setItem(0,EmpireGUI.EmpireGUI);
                event.getPlayer().getInventory().setItem(8,Guide.Guide);
                empireParticlesAPI.setCosmeticItemSlot(event.getPlayer(), 4);
                event.getPlayer().getEnderChest().clear();
                return;
            }

            Empire empire = empireManager.getEmpire(ePlayer.getEPlayerEmpire());
            if(Objects.equals(empire.getIsAlive(), true)){
                empire.getEmpirePlayerList().add(event.getPlayer());
                event.setJoinMessage(prefix + empire.getEmpireColor() + empire.getTeamChatPrefix() + ChatColor.WHITE + event.getPlayer().getName() + " has joined the game.");
                event.getPlayer().setPlayerListName(prefix + empire.getEmpireColor() + empire.getTeamChatPrefix() + ChatColor.WHITE + event.getPlayer().getName());
                NametagEdit.getApi().setNametag(event.getPlayer(),prefix + " " + empire.getEmpireColor(), " " + empire.getTeamChatPrefix());
                gameInstance.getGameScoreboard().addPlayer(player);
                NPC.addJoinPacket(player, this);
            }else{
                ePlayer.setEPlayerEmpire("NEUTRAL");
                event.getPlayer().getInventory().clear();
                event.getPlayer().getEnderChest().clear();
                Bukkit.getScheduler().runTaskLater(this, () -> player.teleport(Bukkit.getServer().getWorld("hubmap").getSpawnLocation()), 5);
                event.getPlayer().setPlayerListName(prefix + ChatColor.WHITE + event.getPlayer().getName());
                event.getPlayer().sendMessage(ChatColor.RED + "Your empire has been captured. Choose another Empire!");
                event.setJoinMessage(prefix + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the game.");
                NametagEdit.getApi().setNametag(event.getPlayer(),prefix + " " + ChatColor.WHITE ,   null  );
                event.getPlayer().getInventory().setItem(0,EmpireGUI.EmpireGUI);
                event.getPlayer().getInventory().setItem(8,Guide.Guide);
                empireParticlesAPI.setCosmeticItemSlot(event.getPlayer(), 4);

            }
        }






    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
       EPlayer ePlayer = ePlayerManager.getEPlayer(event.getPlayer());


        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                //This is where you should do your database interaction
                sqlGetter.addKills(event.getPlayer().getUniqueId(), ePlayer.getTotalKills());
                sqlGetter.addDeaths(event.getPlayer().getUniqueId(), ePlayer.getTotalDeaths());
                sqlGetter.addCaptures(event.getPlayer().getUniqueId(), ePlayer.getTotalCaptures());
            }
        };
        r.runTaskAsynchronously(this);

       if(!Objects.equals(ePlayer.getEPlayerEmpire(),"NEUTRAL")) {
           empireManager.getEmpire(ePlayer.getEPlayerEmpire()).removePlayer(event.getPlayer());
       }
        if(HorsesAlive.containsKey(event.getPlayer().getUniqueId())){
            UUID oldHorseID = HorsesAlive.get(event.getPlayer().getUniqueId());
            Horse oldHorse = (Horse) Bukkit.getEntity(oldHorseID);
            if (oldHorse != null && !oldHorse.isDead()) {
                oldHorse.remove();
            }
            HorsesAlive.remove(event.getPlayer().getUniqueId());
        }
        PacketReaderNPC reader = new PacketReaderNPC(this);
        reader.uninject(event.getPlayer());

    }

    @EventHandler
    public void onPlayerPlaceBlock( BlockPlaceEvent event) {
        Player p = event.getPlayer();


        if(p.getInventory().getItemInMainHand().getItemMeta().equals(Cannon.cannon.getItemMeta())){

            Location location = new Location(event.getBlockPlaced().getLocation().getWorld(),event.getBlockPlaced().getLocation().getX(),event.getBlockPlaced().getLocation().getY(),event.getBlockPlaced().getLocation().getZ());
            location.subtract(0,1,0);
            for(Town town : townManager.getActiveTowns()){
                if(!Objects.equals(town.getGate(),null)) {
                    if (town.getGate().getCannonLocation().equals(location)) {
                        if (ePlayerManager.getEPlayer(event.getPlayer()).getEPlayerEmpire().equals(town.getOwner())) {
                            event.getPlayer().sendMessage(ChatColor.RED + "This is your town!");
                            event.setCancelled(true);
                            return;
                        }
                        if (town.getGate().isCannonPlaced()) {
                            event.getPlayer().sendMessage(ChatColor.RED + "A cannon is already placed!");
                            event.setCancelled(true);
                            return;
                        }

                        if (town.getGate().isGateDestroyed()) {
                            event.getPlayer().sendMessage(ChatColor.RED + "The gate is already destroyed! Go capture!");
                            event.setCancelled(true);
                            return;
                        }

                        town.getGate().setCannonPlaced(true);
                        town.getGate().setCannonHealth(100);
                        BlockData cannon = event.getBlockPlaced().getBlockData();
                        Location l = town.getGate().getGateLocation();
                        Directional directional = (Directional) cannon;
                        BlockFace face;
                        int locZ;
                        int locX;
                        if(l.getZ() < 0){
                            locZ = (int) Math.floor(l.getZ());
                        }else{
                            locZ =(int) Math.ceil(l.getZ());
                        }

                        if(l.getX() < 0){
                            locX = (int) Math.floor(l.getX());
                        }else{
                            locX = (int) Math.ceil(l.getX());
                        }

                        if(event.getBlockPlaced().getZ() < locZ){
                            face = BlockFace.SOUTH;
                        }else if(event.getBlockPlaced().getZ() > locZ){
                            face = BlockFace.NORTH;
                        }else if(event.getBlockPlaced().getX() < locX){
                            face = BlockFace.EAST;
                        }else{
                            face = BlockFace.WEST;
                        }
                        directional.setFacing(face);
                        event.getBlockPlaced().setBlockData(directional);

                        return;
                    }
                }
            }

            for(Empire empire : empireManager.getActiveEmpires()){
                if(!Objects.equals(empire.getGate(),null)) {
                    if (empire.getGate().getCannonLocation().equals(location)) {

                        if (!empire.getIsAlive()) {
                            event.getPlayer().sendMessage(ChatColor.RED + "This empire is already captured!");
                            event.setCancelled(true);
                            return;
                        }


                        if (ePlayerManager.getEPlayer(event.getPlayer()).getEPlayerEmpire().equals(empire.getREFERENCE())) {
                            event.getPlayer().sendMessage(ChatColor.RED + "This is your empire!");
                            event.setCancelled(true);
                            return;
                        }

                        if(!empire.getCanCapture()){
                            event.getPlayer().sendMessage(ChatColor.RED + "This empire can not be captured yet!");
                            event.setCancelled(true);
                            return;
                        }

                        if (empire.getGate().isCannonPlaced()) {
                            event.getPlayer().sendMessage(ChatColor.RED + "A cannon is already placed!");
                            event.setCancelled(true);
                            return;
                        }

                        if (empire.getGate().isGateDestroyed()) {
                            event.getPlayer().sendMessage(ChatColor.RED + "The gate is already destroyed! Go capture!");
                            event.setCancelled(true);
                            return;
                        }

                        String playerEmpireReference = ePlayerManager.getEPlayer(event.getPlayer()).getEPlayerEmpire();
                        Empire playerE = empireManager.getEmpire(playerEmpireReference);
                        if(playerE.getGate().isGateDestroyed() || playerE.getGate().isCannonPlaced()){
                            event.getPlayer().sendMessage(ChatColor.RED + "Your empire's capital is under attack you must go defend!");
                            event.setCancelled(true);
                            return;
                        }

                        empire.getGate().setCannonPlaced(true);
                         empire.getGate().setCannonHealth(100);
                        BlockData cannon = event.getBlockPlaced().getBlockData();
                        Location l = empire.getGate().getGateLocation();
                        Directional directional = (Directional) cannon;
                        BlockFace face;
                        int locZ;
                        int locX;
                        if(l.getZ() < 0){
                            locZ = (int) Math.floor(l.getZ());
                        }else{
                            locZ =(int) Math.ceil(l.getZ());
                        }

                        if(l.getX() < 0){
                            locX = (int) Math.floor(l.getX());
                        }else{
                            locX = (int) Math.ceil(l.getX());
                        }

                        if(event.getBlockPlaced().getZ() < locZ){
                            face = BlockFace.SOUTH;
                        }else if(event.getBlockPlaced().getZ() > locZ){
                            face = BlockFace.NORTH;
                        }else if(event.getBlockPlaced().getX() < locX){
                            face = BlockFace.EAST;
                        }else{
                            face = BlockFace.WEST;
                        }
                        directional.setFacing(face);
                        event.getBlockPlaced().setBlockData(directional);
                        return;
                    }
                }
            }
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cant place a cannon there!");
            return;
        }

        if(!p.hasPermission("empires.build")){
            event.setCancelled(true);
            return;
        }
    }


    @EventHandler
    public void onClick(PlayerInteractEvent event){
        Player p = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(event.getItem() != null){

                if(event.getItem().getItemMeta().equals(CaptureTool.CaptureTool.getItemMeta())){
                             Double location1X =   event.getClickedBlock().getLocation().getX();
                             Double location1Y =   event.getClickedBlock().getLocation().getY();
                             Double location1Z =   event.getClickedBlock().getLocation().getZ();
                           blockLocation = event.getClickedBlock().getLocation();
                            event.getPlayer().sendMessage("Location set to: " + location1X + " ," + location1Y + " , " + location1Z);
                            return;
                }

            }
            

            Block block = event.getClickedBlock();
            if(block.getType() == Material.BIRCH_WALL_SIGN){
                Sign sign = (Sign) block.getState();
                for(Town town :townManager.getActiveTowns()){
                    if(Objects.equals(sign.getLine(0).toUpperCase(),town.getREFERENCE())){
                        if(Objects.equals(ePlayerManager.getEPlayer(p).getEPlayerEmpire(),town.getOwner()) || Objects.equals(town.getOwner(),"NEUTRAL")){

                            BlockData data = sign.getBlockData();
                            if (data instanceof Directional)
                            {
                                Directional directional = (Directional)data;
                                Block blockBehind = block.getRelative(directional.getFacing().getOppositeFace());
                                BlockFace face = block.getFace(blockBehind);

                                Location loc = block.getLocation();
                               Vector dir = face.getDirection();
                                dir.normalize();
                                dir.multiply(6); //5 blocks a way
                                loc.add(dir);
                                Location adjustViewLocation = new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ(), p.getLocation().getYaw(),p.getLocation().getPitch());
                                p.teleport(adjustViewLocation);
                                return;
                            }



                        }else{
                            return;
                        }
                    }
                }

                for(Empire empire :empireManager.getActiveEmpires()){
                    if(Objects.equals(sign.getLine(0).toUpperCase(),empire.getREFERENCE())){
                        if(Objects.equals(ePlayerManager.getEPlayer(p).getEPlayerEmpire(),empire.getREFERENCE()) || !empire.getIsAlive()){

                            BlockData data = sign.getBlockData();
                            if (data instanceof Directional)
                            {
                                Directional directional = (Directional)data;
                                Block blockBehind = block.getRelative(directional.getFacing().getOppositeFace());
                                BlockFace face = block.getFace(blockBehind);

                                Location loc = block.getLocation();
                                Vector dir = face.getDirection();
                                dir.normalize();
                                dir.multiply(10); //10 blocks a way for capital
                                loc.add(dir);
                                Location adjustViewLocation = new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ(), p.getLocation().getYaw(),p.getLocation().getPitch());
                                p.teleport(adjustViewLocation);
                                return;
                            }



                        }else{
                            return;
                        }
                    }
                }


            }
        }

        if(event.getAction() == Action.LEFT_CLICK_BLOCK){
            Block block = event.getClickedBlock();
            if(block.getType() == Material.OBSERVER){
                Location location = new Location(event.getClickedBlock().getLocation().getWorld(),event.getClickedBlock().getLocation().getX(),event.getClickedBlock().getLocation().getY(),event.getClickedBlock().getLocation().getZ());
                location.subtract(0,1,0);
                for(Town town : townManager.getActiveTowns()){
                    if(!Objects.equals(town.getGate(),null)) {
                        if (town.getGate().getCannonLocation().equals(location)) {
                            if (!ePlayerManager.getEPlayer(event.getPlayer()).getEPlayerEmpire().equals(town.getOwner())) {
                                event.getPlayer().sendMessage(ChatColor.RED + "You can't dismantle this cannon!");
                                event.setCancelled(true);
                                return;
                            }

                            town.getGate().damageCannon();
                            event.setCancelled(true);
                        }
                    }
                }

                for(Empire empire : empireManager.getActiveEmpires()){
                    if(!Objects.equals(empire.getGate(),null)) {
                        if (empire.getGate().getCannonLocation().equals(location)) {
                            if (!ePlayerManager.getEPlayer(event.getPlayer()).getEPlayerEmpire().equals(empire.getREFERENCE())) {
                                event.getPlayer().sendMessage(ChatColor.RED + "You can't dismantle this cannon!");
                                event.setCancelled(true);
                                return;
                            }
                            empire.getGate().damageCannon();
                            event.setCancelled(true);

                        }
                    }
                }
            }
        }

        if(event.getItem() != null) {
            if (event.getItem().getItemMeta().equals(EmpireGUI.EmpireGUI.getItemMeta())) {
                guiManager.createInventory();
                Inventory inv = guiManager.getJoinEmpireGUI();

                p.openInventory(inv);
            }

            if (event.getItem().getItemMeta().equals(townGUI.townGUI.getItemMeta())) {
                Inventory inv = guiManager.townSpawnGUI(ePlayerManager.getEPlayer(event.getPlayer()).getEPlayerEmpire(), event.getPlayer().getLocation());
                p.openInventory(inv);
            }

        }


    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        ItemStack stack = item.getItemStack();
        if(item != null) {
            if (item.getItemStack().getItemMeta().equals(EmpireGUI.EmpireGUI.getItemMeta())){
                event.setCancelled(true);
            }
            if (item.getItemStack().getItemMeta().equals(townGUI.townGUI.getItemMeta())){
                event.setCancelled(true);
            }


            if (item.getItemStack().getItemMeta().equals(Guide.Guide.getItemMeta())){
                event.setCancelled(true);
            }
            if (item.getItemStack().getItemMeta().equals(Cosmetics.Cosmetics.getItemMeta())){
                event.setCancelled(true);
            }



        }
    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(event.getClick() == ClickType.NUMBER_KEY){
            event.setCancelled(true);
            player.updateInventory();
        }
        if(event.getCurrentItem() == null){

            return;
        }
        if(event.getCurrentItem().getItemMeta() == null){
            return;
        }
//        if (event.getClickedInventory().getType() ==  In) {
//
//        }

        if(event.getAction() == InventoryAction.SWAP_WITH_CURSOR){
            event.setCancelled(true);
            player.updateInventory();
        }

        if(event.getClickedInventory().getType() == InventoryType.PLAYER){
                if (event.getCurrentItem().getItemMeta().equals(EmpireGUI.EmpireGUI.getItemMeta())) {
                    event.setCancelled(true);
                    guiManager.createInventory();
                    Inventory inv = guiManager.getJoinEmpireGUI();
                    player.openInventory(inv);
                    return;
                }

            if (event.getCurrentItem().getItemMeta().equals(townGUI.townGUI.getItemMeta())) {
                event.setCancelled(true);
                Inventory inv = guiManager.townSpawnGUI(ePlayerManager.getEPlayer(player).getEPlayerEmpire(),player.getLocation());
                player.openInventory(inv);
                return;
            }
            if (event.getCurrentItem().getItemMeta().equals(Guide.Guide.getItemMeta())) {
                event.setCancelled(true);
                player.openBook(Guide.Guide);
                return;
            }

            if(event.getView().getTitle().equals("Towns")) {
                event.setCancelled(true);
            }

            if(Objects.equals(ChatColor.stripColor(event.getView().getTitle()), "Store")) {
                event.setCancelled(true);
            }
            return;
        }

        if(event.getView().getTopInventory().equals(guiManager.getJoinEmpireGUI())) {
            event.setCancelled(true);
            if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
                return;
            }
            String empire = event.getCurrentItem().getItemMeta().getLocalizedName();
            try {
                if(!(gameInstance.getGameState() == GameState.PREGAME) && !(gameInstance.getGameState() == GameState.STOPPED)) {
                    empires.joinEmpire(player, empire);
                    player.closeInventory();

                }else{
                    player.sendMessage(ChatColor.GOLD + "Wait for the game to start!");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(event.getView().getTitle().equals("Towns")) {
            event.setCancelled(true);
            if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
                return;
            }
            if(event.getCurrentItem().getType() == Material.BARRIER){
                return;
            }
            String name = event.getCurrentItem().getItemMeta().getLocalizedName();

            for(Empire empire: empireManager.getActiveEmpires()){
                if(Objects.equals(ePlayerManager.getEPlayer(player).getEPlayerEmpire(),name)){
                    warpCapital(player);
                    player.closeInventory();
                    event.setCancelled(true);
                    return;
                }

                if(Objects.equals(empire.getREFERENCE(),name)){
                    player.setCompassTarget(empire.getEmpireCaptureZone());
                    player.sendMessage(ChatColor.GOLD + "Your compass is now pointing towards " + empire.getEmpireColor() + empire.getName());
                    player.closeInventory();
                    event.setCancelled(true);
                    return;
                }


            }
            Town town = townManager.getTown(name);
            try {
                if(Objects.equals(ePlayerManager.getEPlayer(player).getEPlayerEmpire(),town.getOwner())) {
                    warpTown(player, town);
                    player.closeInventory();
                    event.setCancelled(true);
                    return;
                }else{
                    player.setCompassTarget(town.getTownCaptureZone());
                    player.closeInventory();
                    player.sendMessage(ChatColor.GRAY + "Your empire does not own this town...");
                    player.sendMessage(ChatColor.GOLD + "Your compass is now pointing towards " + town.getName() + "!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if(Objects.equals(ChatColor.stripColor(event.getView().getTitle()), "Store")){
            final ItemStack gold = new ItemStack(Material.GOLD_INGOT);

            event.setCancelled(true);
            if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
                return;
            }
            int price = Integer.parseInt(event.getCurrentItem().getItemMeta().getLocalizedName());
            ItemStack item = event.getCurrentItem();

            if(player.getInventory().containsAtLeast(gold,price)){
                player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, price));

                if(Objects.equals(ChatColor.stripColor(item.getItemMeta().getDisplayName()) , "Cannon")){
                    player.getInventory().addItem(Cannon.cannon);
                }else {

                    ItemStack boughtItem = new ItemStack(item.getType(),item.getAmount());
                    boughtItem.setItemMeta(item.getItemMeta());
                    ItemMeta itemMeta = boughtItem.getItemMeta();
                    itemMeta.setLore(null);
                    boughtItem.setItemMeta(itemMeta);
                    player.getInventory().addItem(boughtItem);

                }

                player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1.0f,1.0f);
                player.closeInventory();
                player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GREEN + "Thank you for the purchase!");

            }else{
                player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.RED + "You do not have enough gold for this purchase!");

            }

        }

        if(event.getView().getTopInventory().containsAtLeast(new ItemStack(Material.SADDLE),1)){
            event.setCancelled(true);
        }

    }


    @EventHandler
    public void onPlayerSay(AsyncPlayerChatEvent event) {

        String prefix = getPlayerPrefix(event.getPlayer());
        if(ePlayerManager.getEPlayer(event.getPlayer()).getEPlayerEmpire().equals("NEUTRAL")){
            event.setFormat(prefix + ChatColor.GRAY  + event.getPlayer().getDisplayName() + ": " + ChatColor.WHITE + event.getMessage());
            return;
        }

        Empire empire = empireManager.getEmpire(ePlayerManager.getEPlayer(event.getPlayer()).getEPlayerEmpire());

        if(inEmpireChat.contains(event.getPlayer())){
            event.setCancelled(true);
            ArrayList<Player> teamPlayers = empire.getEmpirePlayerList();
            for(Player player : teamPlayers){
                player.sendMessage(prefix + empire.getEmpireColor() + empire.getTeamChatPrefix() + " " + event.getPlayer().getDisplayName() + ChatColor.WHITE  + " : " + event.getMessage());
            }

        }else{
            event.setFormat(prefix + empire.getEmpireColor() + empire.getEmpirePrefix() + ChatColor.WHITE + " " + event.getPlayer().getDisplayName() + ChatColor.WHITE  + " : " + event.getMessage() );

        }


    }

    @EventHandler
    public void itemFrameItemRemoval(EntityDamageEvent e) {

        if (e.getEntity() instanceof ItemFrame) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof ItemFrame) {
            Player player = (Player) e.getDamager();
            if(player.hasPermission("empires.build")) return;
            e.setCancelled(true);
        }

        if(e.getEntity() instanceof Player && e.getDamager() instanceof Arrow){
            Player victim = (Player) e.getEntity();
            Player attacker = (Player) ((Arrow) e.getDamager()).getShooter();
            if(!areEnemies(victim,attacker)){
                e.setCancelled(true);
                return;
            }

        }

        if(e.getEntity() instanceof Player && e.getDamager() instanceof Trident){
            Player victim = (Player) e.getEntity();
            Player attacker = (Player) ((Trident) e.getDamager()).getShooter();
            if(!areEnemies(victim,attacker)){
                e.setCancelled(true);
                return;
            }

        }

        if(e.getEntity() instanceof Horse && e.getDamager() instanceof Player){
            Horse horse = (Horse) e.getEntity();
            Player horseOwner = (Player) horse.getOwner();
            Player attacker = (Player) e.getDamager();
            if(!areEnemies(horseOwner,attacker)){
                e.setCancelled(true);
                return;
            }

        }

        if(e.getEntity() instanceof Horse && e.getDamager() instanceof Arrow){
            Horse horse = (Horse) e.getEntity();
            Player horseOwner = (Player) horse.getOwner();
            Player attacker = (Player) ((Arrow) e.getDamager()).getShooter();
            if(!areEnemies(horseOwner,attacker)){
                e.setCancelled(true);
                return;
            }

        }

        if(e.getEntity() instanceof Horse && e.getDamager() instanceof Trident){
            Horse horse = (Horse) e.getEntity();
            Player horseOwner = (Player) horse.getOwner();
            Player attacker = (Player) ((Trident) e.getDamager()).getShooter();
            if(!areEnemies(horseOwner,attacker)){
                e.setCancelled(true);
                return;
            }

        }


        if (!(e.getEntity() instanceof Player && e.getDamager() instanceof Player))
            return;
        Player victim = (Player) e.getEntity();
        Player attacker = (Player) e.getDamager();

        if(!areEnemies(victim,attacker)){
            e.setCancelled(true);

            return;
        }

    }
    @EventHandler
    public void onHorseDeath(EntityDeathEvent event){
        if(event.getEntity() instanceof Horse){
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        EntityDamageEvent.DamageCause damageCause = Objects.requireNonNull(event.getEntity().getLastDamageCause()).getCause();


        List<ItemStack> drops = event.getDrops();

        drops.removeIf(stack -> stack.getType().equals(Material.STONE_SWORD) || stack.getType().equals(Material.STONE_PICKAXE) || stack.getType().equals(Material.BOW) || stack.getType().equals(Material.LEATHER_HELMET) || stack.getType().equals(Material.LEATHER_CHESTPLATE) || stack.getType().equals(Material.LEATHER_LEGGINGS) || stack.getType().equals(Material.LEATHER_BOOTS) || stack.getType().equals(Material.COMPASS) || stack.getType().equals(Material.WRITTEN_BOOK) || stack.getType().equals(Material.BLAZE_POWDER));

        if(damageCause.equals(EntityDamageEvent.DamageCause.VOID)){
            event.setDeathMessage("");
            return;
        }


        if (event.getEntity().getKiller() != null) {
            Empire victimEmpire = empireManager.getEmpire(ePlayerManager.getEPlayer(event.getEntity()).getEPlayerEmpire());
            Empire killerEmpire = empireManager.getEmpire(ePlayerManager.getEPlayer(event.getEntity().getKiller()).getEPlayerEmpire());
            event.getEntity().getKiller().getInventory().addItem(new ItemStack(Material.GOLD_INGOT));
            event.getEntity().getKiller().sendMessage(ChatColor.GREEN + "+1 Gold for killing " + event.getEntity().getName() + "!");
            ePlayerManager.getEPlayer(event.getEntity().getKiller()).addKill();
           ePlayerManager.getEPlayer(event.getEntity()).addDeath();

            String victimPrefix = getPlayerPrefix(event.getEntity()); //Players Rank
            String killerPrefix = getPlayerPrefix(event.getEntity().getKiller());

            if(damageCause == EntityDamageEvent.DamageCause.PROJECTILE){
                event.setDeathMessage(  ChatColor.DARK_RED + "" + ChatColor.BOLD + "🏹 " + victimPrefix + victimEmpire.getEmpireColor() + victimEmpire.getTeamChatPrefix() + ChatColor.WHITE + event.getEntity().getName()  + " was shot by " + killerPrefix + killerEmpire.getEmpireColor() + killerEmpire.getTeamChatPrefix() + ChatColor.WHITE + event.getEntity().getKiller().getName() );

            }else {
                event.setDeathMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "☠ " + victimPrefix + victimEmpire.getEmpireColor() + victimEmpire.getTeamChatPrefix() + ChatColor.WHITE + event.getEntity().getName() + " was slain by " + killerPrefix + killerEmpire.getEmpireColor() + killerEmpire.getTeamChatPrefix() + ChatColor.WHITE + event.getEntity().getKiller().getName());
            }
        }





    }


    @EventHandler(ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        Player player2 = (Player) e.getEnemy();
        if(!areEnemies(player,player2)) {
            e.setCancelled(true);
        }
    }

    // Added 8/25/2020 - Replaces Iron/Gold Blocks with Air -> Inputs Ingot into player inventory.
    @EventHandler
    public void onBreak(final BlockBreakEvent e) { // Initiates upon block break event.
        final Player p = e.getPlayer(); // Gets player in order to get player inventory.
        final Inventory inv = (Inventory)p.getInventory();
        final Block block = e.getBlock(); // Gets block data. (The block broken.)
        final ItemStack iron = new ItemStack(Material.IRON_INGOT);
        final ItemStack gold = new ItemStack(Material.GOLD_INGOT);
        final ItemStack netherite = new ItemStack(Material.NETHERITE_INGOT);
        final ItemStack diamond = new ItemStack(Material.DIAMOND);
        final ItemStack bread = new ItemStack(Material.BREAD);
        final ItemStack arrows = new ItemStack(Material.ARROW,32);

        if(!p.hasPermission("empires.build")) {
            if (!(block.getType() == Material.HAY_BLOCK) && !(block.getType() == Material.IRON_ORE) && !(block.getType() == Material.GOLD_ORE) && !(block.getType() == Material.DIAMOND_ORE) && !(block.getType() == Material.ANCIENT_DEBRIS)) {
                e.setCancelled(true);
                return;
            }
        }

        if (block.getType() == Material.HAY_BLOCK) { // Checks if the block broken is Iron Ore.

            block.setType(Material.AIR); // If true, replaces Iron Ore with Air block. (Removes block.)
            inv.addItem(bread); // Adds iron bar to player's inventory.
            Bukkit.getScheduler().runTaskLater(this, () -> block.setType(Material.HAY_BLOCK), 100);

            return;
        }



        if (block.getType() == Material.IRON_ORE) { // Checks if the block broken is Iron Ore.
            if(townManager.getTownBlocks().containsKey(block.getLocation())) {  //If block is within Hashmap of blocks
                String REFERENCE = townManager.getTownBlocks().get(block.getLocation()); // block's town
                Town town = townManager.getTown(REFERENCE);
                if(Objects.equals( ePlayerManager.getEPlayer(p).getEPlayerEmpire(), town.getOwner())) {
                    block.setType(Material.AIR); // If true, replaces Iron Ore with Air block. (Removes block.)
                    inv.addItem(iron); // Adds iron bar to player's inventory.
                    Bukkit.getScheduler().runTaskLater(this, () -> block.setType(Material.IRON_ORE), 100);
                    //inv.addItem(new ItemStack[] { iron }); // Adds iron bar to player's inventory.
                }else{
                    p.sendMessage(ChatColor.RED + "Your empire must own this town to mine!");
                    e.setCancelled(true);
                }
            }else{
                e.setCancelled(true); //If block isn't in a town it won't be broken
                return;
            }
            return;
        }


        if (block.getType() == Material.GOLD_ORE) { // Checks if the block broken is Gold Ore.
            if(townManager.getTownBlocks().containsKey(block.getLocation())) {  //If block is within Hashmap of blocks
                String REFERENCE = townManager.getTownBlocks().get(block.getLocation()); // block's town
                Town town = townManager.getTown(REFERENCE);
                if(Objects.equals( ePlayerManager.getEPlayer(p).getEPlayerEmpire(), town.getOwner())) {
                    block.setType(Material.AIR); // If true, replaces Iron Ore with Air block. (Removes block.)
                    inv.addItem(gold); // Adds iron bar to player's inventory.
                    Bukkit.getScheduler().runTaskLater(this, () -> block.setType(Material.GOLD_ORE), 100);
                    //inv.addItem(new ItemStack[] { iron }); // Adds iron bar to player's inventory.
                }else{
                    p.sendMessage(ChatColor.RED + "Your empire must own this town to mine!");
                    e.setCancelled(true);
                }
            }else{
                e.setCancelled(true); //If block isn't in a town it won't be broken
                return;
            }
            return;
        }



        if (block.getType() == Material.ANCIENT_DEBRIS) { // Checks if the block broken is Iron Ore.
            if(townManager.getTownBlocks().containsKey(block.getLocation())) {  //If block is within Hashmap of blocks
                String REFERENCE = townManager.getTownBlocks().get(block.getLocation()); // block's town
                Town town = townManager.getTown(REFERENCE);
                if(Objects.equals( ePlayerManager.getEPlayer(p).getEPlayerEmpire(), town.getOwner())) {
                    block.setType(Material.AIR); // If true, replaces Iron Ore with Air block. (Removes block.)
                    inv.addItem(netherite); // Adds iron bar to player's inventory.
                    Bukkit.getScheduler().runTaskLater(this, () -> block.setType(Material.ANCIENT_DEBRIS), 100);
                    //inv.addItem(new ItemStack[] { iron }); // Adds iron bar to player's inventory.
                }else{
                    p.sendMessage(ChatColor.RED + "Your empire must own this town to mine!");
                    e.setCancelled(true);
                }
            }else{
                e.setCancelled(true); //If block isn't in a town it won't be broken
                return;
            }
            return;
        }




        if (block.getType() == Material.DIAMOND_ORE) { // Checks if the block broken is Gold Ore.
            if(townManager.getTownBlocks().containsKey(block.getLocation())) {  //If block is within Hashmap of blocks
                String REFERENCE = townManager.getTownBlocks().get(block.getLocation()); // block's town
                Town town = townManager.getTown(REFERENCE);
                if(Objects.equals( ePlayerManager.getEPlayer(p).getEPlayerEmpire(), town.getOwner())) {
                    block.setType(Material.AIR); // If true, replaces Iron Ore with Air block. (Removes block.)
                    inv.addItem(diamond); // Adds iron bar to player's inventory.
                    Bukkit.getScheduler().runTaskLater(this, () -> block.setType(Material.DIAMOND_ORE), 100);
                    //inv.addItem(new ItemStack[] { iron }); // Adds iron bar to player's inventory.
                }else{
                    p.sendMessage(ChatColor.RED + "Your empire must own this town to mine!");
                    e.setCancelled(true);
                }
            }else{
                e.setCancelled(true); //If block isn't in a town it won't be broken
                return;
            }
            return;
        }

    }

    @EventHandler
    public void onRide(final EntityMountEvent event){
        if(event.getMount() instanceof Horse){
            Horse h = (Horse) event.getMount();
            HorseInventory inv = h.getInventory();
            Player owner = (Player) h.getOwner();
            Player player = (Player) event.getEntity();
            Location location = player.getLocation();
            if(!Objects.equals(player,owner)){
                event.setCancelled(true);
                player.teleport(location);
                player.sendMessage(ChatColor.DARK_RED + "This is not your horse!");
            }

        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (playerTeleport.containsKey(player.getUniqueId())) { // if they are teleporting
            if (event.getTo().getBlockX() != event.getFrom().getBlockX() || event.getTo().getBlockZ() != event.getFrom().getBlockZ()){
                // and they have moved (doesnt account for turning around)
                playerTeleport.replace(player.getUniqueId(), true); // change bool to false
            }
        }
    }
    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        // Checking if the reason we are being kicked is a full server
        if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            // Checking if the player has the specified permission node
            if (event.getPlayer().hasPermission("empires.server.bypass")){
                // If the condition above is true, we execute the following code, that allows the player on the server
                event.allow();
        }else{
                event.setKickMessage(ChatColor.RED + "This server is full!" + ChatColor.GREEN + " You can buy a server bypass at STORE.MCEMPIRES.NET !");

            }
        }

    }



    @EventHandler
    public void onEntityDestroy(HangingBreakByEntityEvent event) {

        Entity entity = event.getEntity();
        Entity player = event.getRemover();

        if (entity instanceof Painting || entity instanceof ItemFrame && player instanceof Player) {
            if (player.hasPermission("empires.build")) return;
        }
        event.setCancelled(true);
    }


    @EventHandler
    public void onNPCClick(RightClickNPC event) {


        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand(); //Destroy NPC
            if (Objects.equals(itemInHand.getItemMeta(),NPCSlayer.NPCSlayer.getItemMeta())) {
                NPC.deleteNPC(event.getNPC(), this);
                event.getPlayer().sendMessage(ChatColor.RED + "You murked that NPC");
                return;
            }

        Inventory inv = guiManager.getStoreInventory();
        event.getPlayer().openInventory(inv);

    }




    /*

      -------------------------------------------------------------------------------





      PLAYER COMMANDS





      -------------------------------------------------------------------------------

   */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] strings) {
        if(!(sender instanceof  Player)){
            return true;
        }
        Player player = (Player) sender;
        if(isInCombat(player)){
            player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.RED + "You are in combat!");
            return true;
        }

        //Capture tool
        if(command.getName().equalsIgnoreCase("capturetool")){
                player.getInventory().addItem(CaptureTool.CaptureTool);
        }

        if(command.getName().equalsIgnoreCase("blockowner")){
           if(!Objects.equals(townManager.getTown(strings[0].toUpperCase()),null) ){
              townManager.getTownBlocks().put(blockLocation,strings[0].toUpperCase());
               player.sendMessage(ChatColor.BLUE + "Block set for " + strings[0].toUpperCase());
           }else{
               player.sendMessage("Invalid town Reference.");
           }
        }
        if(command.getName().equalsIgnoreCase("cannon")){
            player.getInventory().addItem(Cannon.cannon);
        }

        if(command.getName().equalsIgnoreCase("smith")){
            ItemStack item =  player.getInventory().getItemInMainHand();
            Material type = item.getType();
            if(!player.getInventory().containsAtLeast(new ItemStack(Material.NETHERITE_INGOT,1),1)){
                player.sendMessage(ChatColor.RED + "You must have at least one netherite ingot to use /smith.");
                return true;
            }
            if(type.equals(Material.DIAMOND_BOOTS)){
                item.setType(Material.NETHERITE_BOOTS);
                player.getInventory().removeItem(new ItemStack(Material.NETHERITE_INGOT, 1));
                player.sendMessage(ChatColor.BLUE + "Item Upgraded!");

            }else if (type.equals(Material.DIAMOND_LEGGINGS)){
                item.setType(Material.NETHERITE_LEGGINGS);
                player.getInventory().removeItem(new ItemStack(Material.NETHERITE_INGOT, 1));
                player.sendMessage(ChatColor.BLUE + "Item Upgraded!");

            }else if (type.equals(Material.DIAMOND_CHESTPLATE)){
                item.setType(Material.NETHERITE_CHESTPLATE);
                player.getInventory().removeItem(new ItemStack(Material.NETHERITE_INGOT, 1));
                player.sendMessage(ChatColor.BLUE + "Item Upgraded!");

            }else if (type.equals(Material.DIAMOND_HELMET)){
                item.setType(Material.NETHERITE_HELMET);
                player.getInventory().removeItem(new ItemStack(Material.NETHERITE_INGOT, 1));
                player.sendMessage(ChatColor.BLUE + "Item Upgraded!");

            }else if (type.equals(Material.DIAMOND_SWORD)){
                item.setType(Material.NETHERITE_SWORD);
                player.getInventory().removeItem(new ItemStack(Material.NETHERITE_INGOT, 1));
                player.sendMessage(ChatColor.BLUE + "Item Upgraded!");

            } else if (type.equals(Material.DIAMOND_PICKAXE)){
                item.setType(Material.NETHERITE_PICKAXE);
                player.getInventory().removeItem(new ItemStack(Material.NETHERITE_INGOT, 1));
                player.sendMessage(ChatColor.BLUE + "Item Upgraded!");

            }else if (type.equals(Material.DIAMOND_AXE)){
                item.setType(Material.NETHERITE_AXE);
                player.getInventory().removeItem(new ItemStack(Material.NETHERITE_INGOT, 1));
                player.sendMessage(ChatColor.BLUE + "Item Upgraded!");

            }else{
                player.sendMessage(ChatColor.RED + "You can not use /smith on this item!");
                return true;
            }
        }


        if(command.getName().equalsIgnoreCase("markerlist")){

            Collection<AreaMarker> set = dynmapCommonAPI.getMarkerAPI().getMarkerSet("markers").getAreaMarkers();
            for(AreaMarker areaMarker : set){
                String l = areaMarker.getLabel();
                player.sendMessage(l);

                }
            }


        if(command.getName().equalsIgnoreCase("start")) {
            gameInstance.start();
        }

        if(command.getName().equalsIgnoreCase("reloadconfig")) {
            player.sendMessage(ChatColor.GREEN + "Config reloaded!");
            this.reloadConfig();
            this.broadcasts.clear();

            int count = 0;
            if(this.getConfig().getConfigurationSection("broadcasts.") != null) {
                for (String key : this.getConfig().getConfigurationSection("broadcasts.").getKeys(false)) {
                    this.broadcasts.put(count,getConfig().getStringList("broadcasts." + key));
                    count++;
                }
            }

            Bukkit.getScheduler().cancelTask(broadcastTask);
            broadcastTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new AutoBroadcast(this,broadcasts), 60L, 30 * 20);


            guiManager.createStoreGUI();
        }

        if(command.getName().equalsIgnoreCase("stopgame")){
            gameInstance.stop();
        }
        if(command.getName().equalsIgnoreCase("stats")){
            StatHandler stats = new StatHandler(this);
            stats.showStats(player);

        }



        if(command.getName().equalsIgnoreCase("help")){
            player.openBook(Guide.Guide);
        }

        if(command.getName().equalsIgnoreCase("toggleWhitelist")) {
            Bukkit.getServer().setWhitelist(false);
            player.sendMessage(ChatColor.BLUE + "Server Whitelist turned off.");
        }

        if(command.getName().equalsIgnoreCase("resetgame")){
            resetGame();
        }

        if(command.getName().equalsIgnoreCase("resetStatus")) {
            for (Empire empire: empireManager.getActiveEmpires()) {
                empire.setAlive(true);
            }
            player.sendMessage(ChatColor.BLUE + "Empire status reset.");

        }



        if(command.getName().equalsIgnoreCase("horse")) {
            if(HorseCooldown.containsKey(player.getName())){
                //Player in Hashmap
                if(HorseCooldown.get(player.getName()) > System.currentTimeMillis()){
                    //Still has cooldown
                    long timeLeft = (HorseCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                    player.sendMessage(ChatColor.GOLD + "You can use this command again in: " + timeLeft + " seconds");
                    return true;
                }
            }
            if(Objects.equals(ePlayerManager.getEPlayer(player).getEPlayerEmpire() , "NEUTRAL")){
                player.sendMessage(ChatColor.RED + "You can not spawn a horse in lobby!");
                return true;
            }

            HorseCooldown.put(player.getName(),System.currentTimeMillis() + (150 * 1000));

            Horse h = (Horse) player.getWorld().spawn(player.getLocation(), Horse.class);

            h.setTamed(true);
            h.setOwner(player);
            h.setColor(Horse.Color.WHITE);
            h.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            h.setCustomName(player.getName() + "'s Horse");
            h.setCustomNameVisible(true);
            h.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(15.0);
            h.setHealth(15.0);
            h.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
            h.setJumpStrength(0.8);
            h.getInventory().setArmor(new  ItemStack(Material.LEATHER_HORSE_ARMOR));
            if(HorsesAlive.containsKey(player.getUniqueId())){
                UUID oldHorseID = HorsesAlive.get(player.getUniqueId());
                Horse oldHorse = (Horse) Bukkit.getEntity(oldHorseID);
                if(oldHorse != null) {
                    if (!oldHorse.isDead()) {
                        oldHorse.remove();
                    }
                }
                HorsesAlive.remove(player.getUniqueId());

            }
            String group = getPlayerGroup(player);
            if(Objects.equals(group.toLowerCase(),"owner")){
                h.setColor(Horse.Color.BLACK);
                ItemStack armor = new ItemStack(Material.LEATHER_HORSE_ARMOR);
                LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
                meta.setColor(Color.WHITE);
                armor.setItemMeta(meta);
                h.getInventory().setArmor(armor);
            }
            if(Objects.equals(group.toLowerCase(),"chief")){
                h.setColor(Horse.Color.CREAMY);
                ItemStack armor = new ItemStack(Material.LEATHER_HORSE_ARMOR);
                LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
                meta.setColor(Color.GREEN);
                armor.setItemMeta(meta);
                h.getInventory().setArmor(armor);
            }
            if(Objects.equals(group.toLowerCase(),"commander")){
                h.setColor(Horse.Color.CHESTNUT);
                ItemStack armor = new ItemStack(Material.LEATHER_HORSE_ARMOR);
                LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
                meta.setColor(Color.FUCHSIA);
                armor.setItemMeta(meta);
                h.getInventory().setArmor(armor);
            }
            if(Objects.equals(group.toLowerCase(),"general")){
                h.setColor(Horse.Color.GRAY);
                ItemStack armor = new ItemStack(Material.LEATHER_HORSE_ARMOR);
                LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
                meta.setColor(Color.NAVY);
                armor.setItemMeta(meta);
                h.getInventory().setArmor(armor);
            }
            if(Objects.equals(group.toLowerCase(),"emperor")){
                h.setColor(Horse.Color.BLACK);
                ItemStack armor = new ItemStack(Material.LEATHER_HORSE_ARMOR);
                LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
                meta.setColor(Color.WHITE);
                armor.setItemMeta(meta);
                h.getInventory().setArmor(armor);
            }

            UUID horseID = h.getUniqueId();
            HorsesAlive.put(player.getUniqueId(), horseID);

        }

        if(command.getName().equalsIgnoreCase("empirechat")){
            if(inEmpireChat.contains(player)){
                inEmpireChat.remove(player);
                player.sendMessage(ChatColor.GOLD + "Empire chat " + ChatColor.DARK_RED + "Disabled");
            }else{
                inEmpireChat.add(player);
                player.sendMessage(ChatColor.GOLD + "Empire chat " + ChatColor.DARK_GREEN + "Enabled");
            }
        }
        if(command.getName().equalsIgnoreCase("spawn")){ // /spawn for empire
           warpCapital(player);
        }

        if(command.getName().equalsIgnoreCase("setcolors")){ // /spawn for empire
            for(Empire empire: empireManager.getActiveEmpires()){
                setEmpireDynmapColor(empire);
            }
        }

        if(command.getName().equalsIgnoreCase("empiretp")){ // /spawn for empire
            if(strings.length == 1){
                Location worldLocation = Bukkit.getServer().getWorld(strings[0].toLowerCase()).getSpawnLocation();
                player.teleport(worldLocation);
                player.sendMessage(ChatColor.GREEN + "Teleported to " + strings[0]);
            }else{
                
                return true;
            }
        }

        if(command.getName().equalsIgnoreCase("map")){
            player.sendMessage(ChatColor.GOLD + "Here is the map: " + ChatColor.GREEN + "MAP.MCEMPIRES.NET:8123");
            return true;
        }

        if(command.getName().equalsIgnoreCase("list")){
            ArrayList<String> nameList = new ArrayList<String>();

            for (Empire e : empireManager.getActiveEmpires()){
                if(e.getIsAlive()){
                    String empireName = e.getEmpireColor() + e.getName() + ChatColor.GREEN + " Online: " + ChatColor.WHITE + e.getEmpirePlayerList().size();
                    nameList.add(empireName);
                    String names = "";
                    for(Player p : e.getEmpirePlayerList()){
                        EPlayer ePlayer = ePlayerManager.getEPlayer(p);
                        String name = getPlayerPrefix(p) + ChatColor.WHITE + p.getName();
                        names = names + " " + name + ",";
                    }
                    
                    nameList.add(names);
                }
            }

            for(String s : nameList) {
                player.sendMessage(s);
            }
                return true;
        }

        if(command.getName().equalsIgnoreCase("createnpc")){
            if(strings.length == 0){
                NPC.createNPC(this, player, player.getName());
                player.sendMessage(ChatColor.GREEN + "NPC CREATED");
                return true;
            }
            NPC.createNPC(this,player,strings[0]);
            player.sendMessage(ChatColor.GREEN + "NPC CREATED");
            return true;
        }

        if(command.getName().equalsIgnoreCase("npcs")){
            for(EntityPlayer npc : NPC.getNPCs()){
                player.sendMessage(String.valueOf(npc.getUniqueID()));
            }
            return true;
        }

        if(command.getName().equalsIgnoreCase("npcslayer")){
            player.getInventory().addItem(NPCSlayer.NPCSlayer);
            return true;
        }

        if(command.getName().equalsIgnoreCase("pregame")){
            resetGame();
           gameInstance.pregame();
            return true;
        }



            return true;
    }


}
