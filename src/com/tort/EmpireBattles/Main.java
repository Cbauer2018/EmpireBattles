package com.tort.EmpireBattles;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.nametagedit.plugin.NametagEdit;
import com.tort.EmpireBattles.Commands.Empires;

import com.tort.EmpireBattles.Events.PlayerJoinEvents;
import com.tort.EmpireBattles.Events.PlayerRespawnEvents;
import com.tort.EmpireBattles.Files.EmpireDataManager;
import com.tort.EmpireBattles.Files.PlayerDataManager;

import com.tort.EmpireBattles.GUI.GuiManager;
import com.tort.EmpireBattles.Game.GameInstance;
import com.tort.EmpireBattles.Game.ScoreboardInstance;
import com.tort.EmpireBattles.Game.StatHandler;
import com.tort.EmpireBattles.Items.CaptureTool;


import com.tort.EmpireBattles.Items.EmpireGUI;
import com.tort.EmpireBattles.Items.townGUI;
import dev.esophose.playerparticles.api.PlayerParticlesAPI;
import dev.esophose.playerparticles.particles.ParticleEffect;
import dev.esophose.playerparticles.styles.DefaultStyles;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.AreaMarker;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;


public class Main extends JavaPlugin implements Listener {



    private Location blockLocation;
    private ArrayList<Player> inEmpireChat = new ArrayList<>();
    public GameInstance gameInstance;
    public PlayerDataManager playerdata;
    public EmpireDataManager empiredata;

    public ScoreboardInstance scoreboardInstance;
    public LuckPerms api;
    private PlayerParticlesAPI ppAPI;
    public DynmapCommonAPI dynmapCommonAPI;
    public GuiManager guiManager;
    public StatHandler statHandler;

    final public static ChatColor VIKING_CHATCOLOR = ChatColor.DARK_PURPLE;
    final public static ChatColor OTTOMAN_CHATCOLOR = ChatColor.YELLOW;
    final public static ChatColor MONGOL_CHATCOLOR = ChatColor.DARK_BLUE;
    final public static ChatColor ROMAN_CHATCOLOR = ChatColor.DARK_RED;

    final public static int VIKING_MAPCOLOR = 0xA000FF;
    final public static int OTTOMAN_MAPCOLOR = 0xFFFF00;
    final public static int MONGOL_MAPCOLOR = 0x0000FF;
    final public static int ROMAN_MAPCOLOR = 0xFF0000;

    public static Map<Location, String> CaptureBlocks = new HashMap<Location,String>();

    public static Map<String,String> playerTeams = new ConcurrentHashMap<String,String>();
    public static Map<String, String> CaptureOwners = new ConcurrentHashMap<String,String>();
    public static Map<String, Boolean> EmpireStatus = new ConcurrentHashMap<>();
    public Map<String, Location> TownSpawns = new ConcurrentHashMap<>();
    public Map<Player,UUID> HorsesAlive = new ConcurrentHashMap<>();
    public Map<String,Long> HorseCooldown = new ConcurrentHashMap<>();
    public Map<UUID,Boolean> playerTeleport = new ConcurrentHashMap<>();
    public Map<UUID,Integer> playerKills = new ConcurrentHashMap<>();
    public Map<UUID,Integer> playerCaptures = new ConcurrentHashMap<>();


    public static Map<String, Location> CaptureZones = new HashMap<String,Location>();
    public static Map<String, Location> EmpireZones = new HashMap<String,Location>();
    public static Map<String, Location> EmpireSpawns = new HashMap<String, Location>();
    public static Map<String,String> TownType = new HashMap<String,String>();

    public Empires empires;



    @Override
    public void onEnable() {
        //Register Events
        getServer().getPluginManager().registerEvents(new PlayerJoinEvents(), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnEvents(this),this);
        this.getServer().getPluginManager().registerEvents(this,this);
        this.saveDefaultConfig(); // create conf.yml
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
             api = provider.getProvider();

        }

        this.ppAPI = PlayerParticlesAPI.getInstance();
        dynmapCommonAPI = (DynmapCommonAPI) Bukkit.getServer().getPluginManager().getPlugin("dynmap");



        //Data
        this.playerdata = new PlayerDataManager(this);
        this.empiredata = new EmpireDataManager(this);


        //set Executor for commands

        //Stat Handler
        statHandler = new StatHandler(this);

        //Set Empire Commands

        empires = new Empires(this);
        getCommand("empire").setExecutor(empires);
        getCommand("setspawn").setExecutor(empires);




        //Log enabled Plugin
        getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "[Empire Battles]: Plugin is enabled");

        //Create Game Instance
        gameInstance = new GameInstance(this);

        //Item Init
        CaptureTool.init();
        EmpireGUI.init();
        townGUI.init();

        //Enable scoreboard
        scoreboardInstance = new ScoreboardInstance(this);
        scoreboardInstance.setScoreBoard();


        //Enable GUI
        guiManager = new GuiManager();
        guiManager.createInventory();

        //Restore Hashmaps
        this.restoreCbs();

        for (World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (e instanceof LivingEntity) e.remove();
            }
        }




    }

    @Override
    public void onDisable(){
        this.saveCbs();


        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Empire Battles]: Plugin is disabled");


    }

    // Cbs = Capture Blocks
    public void saveCbs(){


        for(Map.Entry<String,Location> entry: CaptureZones.entrySet()){
            double X = entry.getValue().getX();
            double Y = entry.getValue().getY();
            double Z = entry.getValue().getZ();
            this.getConfig().set("Zones." + entry.getKey().toUpperCase() + ".X", X );
            this.getConfig().set("Zones." + entry.getKey().toUpperCase() + ".Y", Y );
            this.getConfig().set("Zones." + entry.getKey().toUpperCase() + ".Z", Z );
            this.getConfig().set("Zones." + entry.getKey().toUpperCase() + ".zoneowner", CaptureOwners.get(entry.getKey()));
            this.getConfig().set("Zones." + entry.getKey().toUpperCase() + ".type", TownType.get(entry.getKey()));
        }

        for(Map.Entry<String,Location> entry: TownSpawns.entrySet()){
            double X = entry.getValue().getX();
            double Y = entry.getValue().getY();
            double Z = entry.getValue().getZ();
            this.getConfig().set("Zones." + entry.getKey().toUpperCase() + ".spawn" + ".X", X );
            this.getConfig().set("Zones." + entry.getKey().toUpperCase() + ".spawn" + ".Y", Y );
            this.getConfig().set("Zones." + entry.getKey().toUpperCase() + ".spawn" + ".Z", Z );
        }

        for(Map.Entry<String,Location> entry: EmpireZones.entrySet()){
            double X = entry.getValue().getX();
            double Y = entry.getValue().getY();
            double Z = entry.getValue().getZ();
            this.empiredata.getConfig().set(entry.getKey().toUpperCase() + ".capture"+".X", X );
            this.empiredata.getConfig().set( entry.getKey().toUpperCase() + ".capture"+ ".Y", Y );
            this.empiredata.getConfig().set( entry.getKey().toUpperCase() + ".capture"+ ".Z", Z );
            this.empiredata.getConfig().set(entry.getKey().toUpperCase() + ".status", EmpireStatus.get(entry.getKey()) );
            this.empiredata.saveConfig();

        }
        for(Map.Entry<Location, String> entry: CaptureBlocks.entrySet()){
            double X = entry.getKey().getX();
            double Y = entry.getKey().getY();
            double Z = entry.getKey().getZ();

            int xname = (int) entry.getKey().getX();  // the "." messes up the config file so a integer must be used
            int yname = (int) entry.getKey().getY();
            int zname = (int) entry.getKey().getZ();
            String blockname = String.valueOf(xname) + String.valueOf(yname) + String.valueOf(zname) ;
            this.getConfig().set("blocks." + blockname + ".X", X );
            this.getConfig().set("blocks." + blockname + ".Y", Y );
            this.getConfig().set("blocks." + blockname + ".Z", Z );
            this.getConfig().set("blocks." + blockname + ".town", entry.getValue() );
        }

        this.saveConfig();
    }

    // Cbs = Capture Blocks
    public void restoreCbs(){

        Collection<String> zones = this.getConfig().getConfigurationSection("Zones.").getKeys(false);
        for(String zone : zones){
            double X =  this.getConfig().getDouble("Zones." + zone + ".X" );
            double Y =  this.getConfig().getDouble("Zones." + zone + ".Y" );
            double Z =  this.getConfig().getDouble("Zones." + zone + ".Z" );
            String zoneowner = this.getConfig().getString("Zones." + zone + ".zoneowner");
            double spawnX = this.getConfig().getDouble("Zones." + zone + ".spawn" + ".X");
            double spawnY = this.getConfig().getDouble("Zones." + zone + ".spawn" + ".Y");
            double spawnZ= this.getConfig().getDouble("Zones." + zone + ".spawn" + ".Z");
            String type = this.getConfig().getString("Zones." + zone + ".type");


            Location capLocation = new Location(Bukkit.getServer().getWorld("world"),X,Y,Z);
            CaptureZones.put(zone,capLocation);
            getLogger().log(Level.INFO, "Put " + zone);
            CaptureOwners.put(zone,zoneowner);

            Location spawnLocation = new Location(Bukkit.getServer().getWorld("world"),spawnX,spawnY,spawnZ);
            TownSpawns.put(zone, spawnLocation);
            TownType.put(zone,type);
        }

        Collection<String> Empires = this.empiredata.getConfig().getKeys(false);
            for(String empire: Empires){
                Double X = empiredata.getConfig().getDouble(empire + ".spawnpoint" + ".X"); //Get spawn location from file
                Double Y = empiredata.getConfig().getDouble(empire + ".spawnpoint" + ".Y");
                Double Z = empiredata.getConfig().getDouble(empire + ".spawnpoint" + ".Z");
                Float yaw = Float.valueOf(empiredata.getConfig().getString(empire + ".spawnpoint" + ".yaw"));
                Float pitch = Float.valueOf(empiredata.getConfig().getString(empire + ".spawnpoint" + ".pitch"));


                double capX =  this.empiredata.getConfig().getDouble(empire+ ".capture"+".X"); // Get empire's capture zone from file
                double capY = this.empiredata.getConfig().getDouble(empire+ ".capture"+".Y");
                double capZ = this.empiredata.getConfig().getDouble(empire+ ".capture"+".Z");

                Boolean status = this.empiredata.getConfig().getBoolean(empire + ".status"); //Get empire's status from file;

                Location location = new Location(Bukkit.getServer().getWorld("world"),X,Y,Z,yaw,pitch);
                Location capLocation = new Location(Bukkit.getServer().getWorld("world"),capX,capY,capZ);
                EmpireSpawns.put(empire,location);
                EmpireZones.put(empire,capLocation);
                EmpireStatus.put(empire, status);

            }
        Collection<String> blocks = this.getConfig().getConfigurationSection("blocks.").getKeys(false);
            for(String block : blocks){

                double X = this.getConfig().getDouble("blocks." + block + ".X");
                double Y =this.getConfig().getDouble("blocks." + block + ".Y" );
                double Z =this.getConfig().getDouble("blocks." + block + ".Z");
                String town =this.getConfig().getString("blocks." + block + ".town");

                Location blockLocation = new Location(getServer().getWorld("world"),X,Y,Z);
                CaptureBlocks.put(blockLocation,town);
            }

    }

     public static String getTeam(String playerID){ //get playerID from hashmap
        return playerTeams.get(playerID);
    }
    public static Boolean getStatus(String empire){ //get Empire status
        return EmpireStatus.get(empire.toUpperCase());
    }
    public static Boolean isGameDone(){
        int empiresAlive = 0;
        for(Map.Entry<String,Boolean> entry: EmpireStatus.entrySet()){
            if(!entry.getValue()){
                empiresAlive++;
            }
        }

        if(empiresAlive == 1){
            return true;
        }
        return false;
    }

    public static String getWinner(){
        for(Map.Entry<String,Boolean> entry: EmpireStatus.entrySet()){
            if(!entry.getValue()){
                return entry.getKey();
            }
        }
        return "null";
    }







    public static void setTeam(Player player, String empire){
        if(Main.playerTeams.containsKey(player.getUniqueId().toString())) {
            Main.playerTeams.replace(player.getUniqueId().toString(), empire.toUpperCase());
        }else{
            Main.playerTeams.put(player.getUniqueId().toString(), empire.toUpperCase());
        }
    }

    public static void setStatus(String empire, Boolean status){
        if(Main.EmpireStatus.containsKey(empire)) {
            Main.EmpireStatus.replace(empire, status);
        }else{
            Main.EmpireStatus.put(empire, status);
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event){
        if(event.getResult() == PlayerLoginEvent.Result.KICK_FULL){
            if(event.getPlayer().hasPermission("empires.server.bypass")){
                event.allow();
            }
        }

    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {


        if(gameInstance.isGameStarted()) {
            gameInstance.setPlayerScoreBoard(event.getPlayer());
        }

         String prefix = getPlayerPrefix(event.getPlayer());

        playerdata.reloadConfig();

        if(playerdata.getConfig().contains("players."+ event.getPlayer().getUniqueId().toString() + ".empire") || Main.playerTeams.containsKey(event.getPlayer().getUniqueId().toString()) ) {

            if(Objects.equals(playerdata.getConfig().getString("players."+ event.getPlayer().getUniqueId().toString() + ".empire"),"NEUTRAL")){
                event.getPlayer().getInventory().clear();
                event.getPlayer().teleport(getServer().getWorld("world").getSpawnLocation());
                event.getPlayer().setPlayerListName(prefix + ChatColor.WHITE + event.getPlayer().getName());
                event.setJoinMessage(prefix + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the game.");
                NametagEdit.getApi().setNametag(event.getPlayer(),prefix + " " + ChatColor.WHITE ,   null  );
                event.getPlayer().getInventory().setItem(0,EmpireGUI.EmpireGUI);
                event.getPlayer().getEnderChest().clear();
                return;
            }



            if (!Main.playerTeams.containsKey(event.getPlayer().getUniqueId().toString())) {
                playerTeams.put(event.getPlayer().getUniqueId().toString(), playerdata.getConfig().getString("players." + event.getPlayer().getUniqueId().toString() + ".empire"));
            }
            String playerEmpire = playerTeams.get(event.getPlayer().getUniqueId().toString());


            if (Objects.equals(EmpireStatus.get(playerEmpire), false) ) {
                empires.addPlayer(event.getPlayer(), playerEmpire);
                if (Objects.equals(playerTeams.get(event.getPlayer().getUniqueId().toString()), "MONGOLS")) {
                    event.setJoinMessage(prefix + ChatColor.GRAY + "[" + ChatColor.DARK_BLUE + "M" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the game.");
                    event.getPlayer().setPlayerListName(prefix + ChatColor.DARK_BLUE + "[Mongol] " + ChatColor.WHITE + event.getPlayer().getName());
                    NametagEdit.getApi().setNametag(event.getPlayer(),prefix + " " + ChatColor.DARK_BLUE  ,   null  );



                } else if (Objects.equals(playerTeams.get(event.getPlayer().getUniqueId().toString()), "OTTOMANS")) {
                    event.setJoinMessage(prefix + ChatColor.GRAY + "[" + ChatColor.YELLOW + "O" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the game. ");
                    event.getPlayer().setPlayerListName(prefix + ChatColor.YELLOW + "[Ottoman] " + ChatColor.WHITE + event.getPlayer().getName());
                    NametagEdit.getApi().setNametag(event.getPlayer(),prefix + " " + ChatColor.YELLOW  ,   null  );

                } else if (Objects.equals(playerTeams.get(event.getPlayer().getUniqueId().toString()), "ROMANS")) {
                    event.setJoinMessage(prefix + ChatColor.GRAY + "[" + ChatColor.DARK_RED + "R" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the game. ");
                    event.getPlayer().setPlayerListName(prefix + ChatColor.DARK_RED + "[Roman] " + ChatColor.WHITE + event.getPlayer().getName());
                    NametagEdit.getApi().setNametag(event.getPlayer(),prefix + " " + ChatColor.DARK_RED  ,   null  );

                } else {
                    event.setJoinMessage(prefix + ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "V" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the game. ");
                    event.getPlayer().setPlayerListName(prefix + ChatColor.DARK_PURPLE + "[Viking] " + ChatColor.WHITE + event.getPlayer().getName());
                    NametagEdit.getApi().setNametag(event.getPlayer(),prefix + " " + ChatColor.DARK_PURPLE  ,   null  );

                }

            }else{
                Main.setTeam(event.getPlayer(),"NEUTRAL");
                event.getPlayer().getInventory().clear();
                event.getPlayer().getEnderChest().clear();
                event.getPlayer().teleport(Bukkit.getServer().getWorld("world").getSpawnLocation());
                event.getPlayer().setPlayerListName(prefix + ChatColor.WHITE + event.getPlayer().getName());
                event.getPlayer().sendMessage(ChatColor.RED + "Your empire has been captured. Choose another Empire!");
                event.setJoinMessage(prefix + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the game.");
                NametagEdit.getApi().setNametag(event.getPlayer(),prefix + " " + ChatColor.WHITE ,   null  );
                event.getPlayer().getInventory().setItem(0,EmpireGUI.EmpireGUI);


            }


            } else {
                event.getPlayer().getInventory().clear();
                event.getPlayer().teleport(getServer().getWorld("world").getSpawnLocation());
                event.getPlayer().setPlayerListName(prefix + ChatColor.WHITE + event.getPlayer().getName());
                event.setJoinMessage(prefix + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the game.");
                NametagEdit.getApi().setNametag(event.getPlayer(),prefix + " " + ChatColor.WHITE ,   null  );
                event.getPlayer().getInventory().setItem(0,EmpireGUI.EmpireGUI);
                event.getPlayer().getEnderChest().clear();
            }



    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        empires.removePlayer(event.getPlayer());//Remove from empire's array list
        if(HorsesAlive.containsKey(event.getPlayer())){
            UUID oldHorseID = HorsesAlive.get(event.getPlayer());
            Horse oldHorse = (Horse) Bukkit.getEntity(oldHorseID);
            if (oldHorse != null && !oldHorse.isDead()) {
                oldHorse.remove();
            }
            HorsesAlive.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event){
        Player p = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(event.getItem() != null){

                if(event.getItem().getItemMeta().equals(CaptureTool.CaptureTool.getItemMeta())){
                             Double location1X =   event.getClickedBlock().getLocation().getX();
                             Double location1Y =   event.getClickedBlock().getLocation().getY();
                             Double location1Z =   event.getClickedBlock().getLocation().getZ();
                           blockLocation = event.getClickedBlock().getLocation();
                            event.getPlayer().sendMessage("Location set to:" + location1X + "," + location1Y + "," + location1Z);
                            return;
                }

            }

            Block block = event.getClickedBlock();
            final ItemStack gold = new ItemStack(Material.GOLD_INGOT);
            final ItemStack arrows = new ItemStack(Material.ARROW,16);
            final Inventory inv = (Inventory)p.getInventory();
            if(block.getType() == Material.OAK_WALL_SIGN){
                Sign sign = (Sign) block.getState();
                if(sign.getLine(0).equals("arrows")){
                    if(inv.containsAtLeast(gold,10)){
                        inv.removeItem(new ItemStack(Material.GOLD_INGOT, 10));
                        inv.addItem(arrows);
                        p.sendMessage(ChatColor.GREEN + "16 arrows purchased!");
                    }else{
                        p.sendMessage(ChatColor.RED + "You must have 10 gold to buy arrows!");
                    }

                }
            }

        }

        if(event.getItem() != null) {
            if (event.getItem().getItemMeta().equals(EmpireGUI.EmpireGUI.getItemMeta())) {
                Inventory inv = GuiManager.getJoinEmpireGUI();
                p.openInventory(inv);
            }

            if (event.getItem().getItemMeta().equals(townGUI.townGUI.getItemMeta())) {
                Inventory inv = guiManager.townSpawnGUI(getTeam(event.getPlayer().getUniqueId().toString()));
                p.openInventory(inv);
            }

        }


    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        if(item != null) {
            if (item.getItemStack().getItemMeta().equals(EmpireGUI.EmpireGUI.getItemMeta())){
                event.setCancelled(true);
            }
            if (item.getItemStack().getItemMeta().equals(townGUI.townGUI.getItemMeta())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getCurrentItem() == null){
            return;
        }
        if(event.getCurrentItem().getItemMeta() == null){
            return;
        }
//        if (event.getClickedInventory().getType() ==  In) {
//
//        }

        Player player = (Player) event.getWhoClicked();
        if(event.getClickedInventory().getType() == InventoryType.PLAYER){
                if (event.getCurrentItem().getItemMeta().equals(EmpireGUI.EmpireGUI.getItemMeta())) {
                    event.setCancelled(true);
                    Inventory inv = GuiManager.getJoinEmpireGUI();
                    player.openInventory(inv);
                    return;
                }

            if (event.getCurrentItem().getItemMeta().equals(townGUI.townGUI.getItemMeta())) {
                event.setCancelled(true);
                Inventory inv = guiManager.townSpawnGUI(getTeam(player.getUniqueId().toString()));
                player.openInventory(inv);
                return;
            }
            if(event.getView().getTitle().equals("Towns")) {
                event.setCancelled(true);
            }
            return;
        }

        if(event.getView().getTopInventory().equals(GuiManager.getJoinEmpireGUI())) {
            event.setCancelled(true);
            if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
                return;
            }
            String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
            String empire = ChatColor.stripColor(displayName).replaceAll("\\s+", "");
            try {
                empires.joinEmpire(player, empire);
                player.closeInventory();
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
            String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
            String town = ChatColor.stripColor(displayName).replaceAll("\\s+", "");
            try {
                warpTown(player,town);
                player.closeInventory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(event.getView().getTopInventory().containsAtLeast(new ItemStack(Material.SADDLE),1)){
            event.setCancelled(true);
        }



    }



    @EventHandler
    public void onPlayerSay(AsyncPlayerChatEvent event) {

        String prefix = getPlayerPrefix(event.getPlayer());

        if (Objects.equals(playerTeams.get(event.getPlayer().getUniqueId().toString()), "MONGOLS")) {  //If a MONGOL player
            if(inEmpireChat.contains(event.getPlayer())){//If empire chat is enabled
                event.setCancelled(true); //Cancel event for everyone. Only send to specific players
                ArrayList<Player> players = empires.getPlayerList("MONGOLS");

                for (Player player : players) {
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_BLUE + "M" + ChatColor.GRAY + "] "+ ChatColor.DARK_BLUE + event.getPlayer().getDisplayName() + ChatColor.WHITE  + ": " + event.getMessage());
                }

            }else {//If empire chat is disabled
                event.setFormat(prefix + ChatColor.GRAY + "[" + ChatColor.DARK_BLUE + "MONGOL" + ChatColor.GRAY + "] " + ChatColor.WHITE + event.getPlayer().getDisplayName() + ": " + event.getMessage());
            }




        } else if (Objects.equals(playerTeams.get(event.getPlayer().getUniqueId().toString()), "OTTOMANS")) { //If a OTTOMAN player

            if(inEmpireChat.contains(event.getPlayer())){//If empire chat is enabled
                event.setCancelled(true); //Cancel event for everyone. Only send to specific players
                ArrayList<Player> players = empires.getPlayerList("OTTOMANS");
                for (Player player : players) {
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "O" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getDisplayName() + ChatColor.WHITE + ": " + event.getMessage());
                }
            }else {//If empire chat is disabled
                event.setFormat(prefix + ChatColor.GRAY + "[" + ChatColor.YELLOW + "OTTOMAN" + ChatColor.GRAY + "] " + ChatColor.WHITE + event.getPlayer().getDisplayName() + ": " + event.getMessage());
            }



        } else if (Objects.equals(playerTeams.get(event.getPlayer().getUniqueId().toString()), "ROMANS")) {
            if(inEmpireChat.contains(event.getPlayer())){ //If empire chat is enabled
                event.setCancelled(true); //Cancel event for everyone. Only send to specific players
                ArrayList<Player> players = empires.getPlayerList("ROMANS");
                for (Player player : players) {
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_RED + "R" + ChatColor.GRAY + "] " + ChatColor.DARK_RED + event.getPlayer().getDisplayName() + ChatColor.WHITE + ": " + event.getMessage());
                }
            }else { //If empire chat is disabled
                event.setFormat(prefix + ChatColor.GRAY + "[" + ChatColor.DARK_RED + "ROMAN" + ChatColor.GRAY + "] " + ChatColor.WHITE + event.getPlayer().getDisplayName() + ": " + event.getMessage());
            }



        } else if (Objects.equals(playerTeams.get(event.getPlayer().getUniqueId().toString()), "VIKINGS")) {
            if(inEmpireChat.contains(event.getPlayer())){//If empire chat is enabled
                event.setCancelled(true); //Cancel event for everyone. Only send to specific players
                ArrayList<Player> players = empires.getPlayerList("VIKINGS");
                for (Player player : players) {
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "V" + ChatColor.GRAY + "] " + ChatColor.DARK_PURPLE + event.getPlayer().getDisplayName() + ChatColor.WHITE + ": " + event.getMessage());
                }
                }else {
                event.setFormat(prefix + ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "VIKING" + ChatColor.GRAY + "] " + ChatColor.WHITE + event.getPlayer().getDisplayName() + ": " + event.getMessage());
            }
        } else {//If empire chat is disabled

            event.setFormat(prefix + ChatColor.GRAY  + event.getPlayer().getDisplayName() + ": " + ChatColor.WHITE + event.getMessage());
        }

    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Arrow){
            Player victim = (Player) e.getEntity();
            Player attacker = (Player) ((Arrow) e.getDamager()).getShooter();
            if(!areEnemies(victim,attacker)){
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


        List<ItemStack> drops = event.getDrops();

        ListIterator<ItemStack> litr = drops.listIterator();

        while( litr.hasNext() ) {
            ItemStack stack = litr.next();

            if( stack.getType().equals( Material.STONE_SWORD ) || stack.getType().equals( Material.STONE_PICKAXE ) || stack.getType().equals( Material.BOW ) || stack.getType().equals( Material.LEATHER_HELMET ) || stack.getType().equals( Material.LEATHER_CHESTPLATE ) || stack.getType().equals( Material.LEATHER_LEGGINGS )|| stack.getType().equals( Material.LEATHER_BOOTS )|| stack.getType().equals( Material.COMPASS ) )
            {
                litr.remove();
            }
        }

        statHandler.addDeath(event.getEntity());
        playerdata.saveConfig();
        if (event.getEntity() instanceof Player && event.getEntity().getKiller() instanceof Player) {
            String victimEmpire = getTeam(event.getEntity().getUniqueId().toString());
            String killerEmpire = getTeam(event.getEntity().getKiller().getUniqueId().toString());

            String victimEmpirePrefix = getEmpirePrefix(victimEmpire);
            String killerEmpirePrefix = getEmpirePrefix(killerEmpire);

            String victimPrefix = getPlayerPrefix(event.getEntity()); //Players Rank
            String killerPrefix = getPlayerPrefix(event.getEntity().getKiller());
            event.setDeathMessage( victimPrefix + victimEmpirePrefix + ChatColor.WHITE + event.getEntity().getName()  + " was killed by " + killerPrefix + killerEmpirePrefix + ChatColor.WHITE + event.getEntity().getKiller().getName());
            statHandler.addDeath(event.getEntity());
            statHandler.addKill(event.getEntity().getKiller());
            statHandler.SaveData();

        }



    }

    @EventHandler(ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        Player player2 = (Player) e.getEnemy();
        String playerTeam = getTeam(player.getUniqueId().toString());
        String player2Team = getTeam(player2.getUniqueId().toString());
        if(Objects.equals(playerTeam,player2Team)) {
            e.setCancelled(true);
        }
    }

    public boolean isInCombat(Player player) {
        // Make sure to check that CombatLogX is enabled before using it for anything.
        ICombatLogX plugin = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
        ICombatManager combatManager = plugin.getCombatManager();
        return combatManager.isInCombat(player);
    }


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
           if(CaptureOwners.containsKey(strings[0].toUpperCase())){
               CaptureBlocks.put(blockLocation,strings[0].toUpperCase());
               player.sendMessage(ChatColor.BLUE + "Block set for " + strings[0].toUpperCase());
           }else{
               player.sendMessage("Invalid town name.");
           }
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




        if(command.getName().equalsIgnoreCase("warp")){
            if(TownSpawns.containsKey(strings[0].toUpperCase())){
                if(Objects.equals(getTeam(player.getUniqueId().toString()),CaptureOwners.get(strings[0].toUpperCase()))) {
                    warpTown(player,strings[0].toUpperCase());
                }else{
                    player.sendMessage(ChatColor.RED + " Your team does not own this town.");
                }
            }else{
                player.sendMessage(ChatColor.RED + " Invalid town name.");
            }



        }
        if(command.getName().equalsIgnoreCase("particle")){
            if(strings.length > 0) {
                if (strings[0].toLowerCase().equals("on")) {
                    ppAPI.removeActivePlayerParticles(player,DefaultStyles.FEET);
                    ppAPI.addActivePlayerParticle(player, ParticleEffect.CAMPFIRE_COSY_SMOKE, DefaultStyles.FEET);
                    player.sendMessage("Particles Enabled");
                }
                if (strings[0].toLowerCase().equals("off")) {
                    ppAPI.removeActivePlayerParticles(player,DefaultStyles.FEET);
                    player.sendMessage("Particles Disabled");
                }
            }else{
                player.sendMessage(ChatColor.RED + "To enable the particles use the command /particle on. To disable the particle use /particle off");

            }

        }

        //Town COMMANDS
        if(command.getName().equalsIgnoreCase("town")){
            if(strings[1].equals("setCapture")){
                CaptureZones.put(strings[0].toUpperCase(), player.getLocation().add(0,3,0));
                CaptureOwners.put(strings[0].toUpperCase(), "NEUTRAL");
                player.sendMessage(ChatColor.BLUE + "Capture Zone for " + strings[0] + " created!");
            }
            if(strings[1].equals("setSpawn")){
                TownSpawns.put(strings[0].toUpperCase(), player.getLocation());
                player.sendMessage(ChatColor.BLUE + "Town Spawn for " + strings[0] + " is set!");
            }

            if(strings[1].equals("settype")){
                if(TownSpawns.containsKey(strings[0].toUpperCase())){
                    if(strings[2].toLowerCase().equals("iron") || strings[2].toLowerCase().equals("gold") || strings[2].toLowerCase().equals("diamond")|| strings[2].toLowerCase().equals("netherite")) {
                        String type = strings[2].toLowerCase();
                        TownType.put(strings[0].toUpperCase(), type);
                        player.sendMessage(ChatColor.BLUE + strings[0] + " type has been set to " + strings[2]);
                    }else{
                        player.sendMessage(ChatColor.RED + " Invalid type.");
                    }
                }else{
                    player.sendMessage(ChatColor.RED + " Invalid town name.");
                }
            }

        }

        if(command.getName().equalsIgnoreCase("start")) {
            gameInstance.start();
        }
        if(command.getName().equalsIgnoreCase("stopgame")){
            gameInstance.stop();
        }
        if(command.getName().equalsIgnoreCase("stats")){
            StatHandler stats = new StatHandler(this);
            stats.showStats(player);
        }
        if(command.getName().equalsIgnoreCase("resetPlayers")){
            playerTeams.clear();
            Collection<String> players = this.playerdata.getConfig().getConfigurationSection("players").getKeys(false);
            for(String uuid: players){
                player.sendMessage(uuid);
                statHandler.resetPlayerTeam(uuid);
                statHandler.resetGameStats(uuid);
                statHandler.SaveData();
            }




         }

        if(command.getName().equalsIgnoreCase("toggleWhitelist")) {
            Bukkit.getServer().setWhitelist(false);
            player.sendMessage(ChatColor.BLUE + "Server Whitelist turned off.");
        }

        if(command.getName().equalsIgnoreCase("resetStatus")) {
            for (Map.Entry<String, Boolean> entry : EmpireStatus.entrySet()) {
                entry.setValue(false);
            }
            player.sendMessage(ChatColor.BLUE + "Empire status reset.");

        }

        if(command.getName().equalsIgnoreCase("resetTowns")) {
            for(Map.Entry<String,String> entry : CaptureOwners.entrySet()){
                entry.setValue("NEUTRAL");
                changeTownColor(entry.getKey().toUpperCase(),"NEUTRAL");
            }
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.GOLD + "MCE" + ChatColor.DARK_RED + "] " + ChatColor.GOLD + "Towns are reset.");
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

            HorseCooldown.put(player.getName(),System.currentTimeMillis() + (60 * 1000));

            Horse h = (Horse) player.getWorld().spawn(player.getLocation(), Horse.class);

            h.setTamed(true);
            h.setOwner(player);
            h.setColor(Horse.Color.WHITE);
            h.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            h.setCustomName(player.getName() + "'s Horse");
            h.setCustomNameVisible(true);
            h.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(22.0);
            h.setHealth(22.0);
            h.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
            h.setJumpStrength(50);
            h.getInventory().setArmor(new  ItemStack(Material.LEATHER_HORSE_ARMOR));
            player.sendMessage(h.getInventory().getType().toString());
            if(HorsesAlive.containsKey(player)){
                UUID oldHorseID = HorsesAlive.get(player);
                Horse oldHorse = (Horse) Bukkit.getEntity(oldHorseID);
                if(oldHorse != null) {
                    if (!oldHorse.isDead()) {
                        oldHorse.remove();
                    }
                }
                HorsesAlive.remove(player);

            }
            String group = getPlayerGroup(player);
            if(Objects.equals(group.toLowerCase(),"owner")){
                h.setColor(Horse.Color.BLACK);
                h.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5);
                h.setJumpStrength(100);
                ItemStack armor = new ItemStack(Material.LEATHER_HORSE_ARMOR);
                LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
                meta.addEnchant(Enchantment.LUCK,1,false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
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
            HorsesAlive.put(player, horseID);

        }

        if(command.getName().equalsIgnoreCase("caplist")){

            if(!CaptureZones.isEmpty()) {
                for (Map.Entry<String, Location> entry : CaptureZones.entrySet()) {

                    player.sendMessage(entry.getKey());
                }
            }else{
                player.sendMessage("Map is empty");

            }        }

        if(command.getName().equalsIgnoreCase("empirelist")){
            int OTTOMANS = 0;
            int MONGOLS = 0;
            int ROMANS = 0;
            int VIKINGS = 0;

            for(Map.Entry<String, String> entry: Main.CaptureOwners.entrySet()){
                if(Objects.equals(entry.getValue(),"OTTOMANS")){
                    OTTOMANS++;
                }else if(Objects.equals(entry.getValue(),"MONGOLS")){
                    MONGOLS++;
                }else if(Objects.equals(entry.getValue(),"ROMANS")){
                    ROMANS++;
                }else{
                    VIKINGS++;
                }
            }
            player.sendMessage(ChatColor.YELLOW + "Ottoman towns" + ChatColor.WHITE + ": " + OTTOMANS);
            player.sendMessage(ChatColor.DARK_BLUE + "Mongol towns" + ChatColor.WHITE + ": " + MONGOLS);
            player.sendMessage(ChatColor.DARK_RED + "Roman towns" + ChatColor.WHITE + ": " + ROMANS);
            player.sendMessage(ChatColor.DARK_PURPLE + "Viking towns" + ChatColor.WHITE + ": " + VIKINGS);
        }
        if(command.getName().equalsIgnoreCase("team")){
            player.sendMessage(playerTeams.get(player.getUniqueId().toString()));
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
            if(!Objects.equals(getTeam(player.getUniqueId().toString()), "NEUTRAL") && playerTeams.containsKey(player.getUniqueId().toString())) { // If player isnt neutral and player is on team

                String empire = getTeam(player.getUniqueId().toString());//empire of team
                Location location = Main.EmpireSpawns.get(empire); // Empire's spawn
                if(!playerTeleport.containsKey(player.getUniqueId())) {

                    int delay = 10; //delay in seconds
                    player.sendMessage(ChatColor.GOLD + "Teleporting to the capital in 10 seconds. Don't move!");
                    playerTeleport.put(player.getUniqueId(), false); //map storing their id to whether they moved

                    Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
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
                }else {
                    player.sendMessage(ChatColor.RED + "You are already teleporting!");
                }

            }else{
                player.sendMessage(ChatColor.RED + "Join an empire to use /spawn!");
            }
        }


            return true;
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
        final ItemStack arrows = new ItemStack(Material.ARROW,32);
        if (block.getType() == Material.IRON_ORE) { // Checks if the block broken is Iron Ore.
            if(CaptureBlocks.containsKey(block.getLocation())) {  //If block is within Hashmap of blocks
                String town = CaptureBlocks.get(block.getLocation()); // block's town

                if(Objects.equals(getTeam(p.getUniqueId().toString()), CaptureOwners.get(town))) {
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
            if(CaptureBlocks.containsKey(block.getLocation())) {  //If block is within Hashmap of blocks
                String town = CaptureBlocks.get(block.getLocation()); // block's town

                if(Objects.equals(getTeam(p.getUniqueId().toString()), CaptureOwners.get(town))) {
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
            if(CaptureBlocks.containsKey(block.getLocation())) {  //If block is within Hashmap of blocks
                String town = CaptureBlocks.get(block.getLocation()); // block's town

                if(Objects.equals(getTeam(p.getUniqueId().toString()), CaptureOwners.get(town))) {
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
                if(CaptureBlocks.containsKey(block.getLocation())) {  //If block is within Hashmap of blocks
                    String town = CaptureBlocks.get(block.getLocation()); // block's town

                    if(Objects.equals(getTeam(p.getUniqueId().toString()), CaptureOwners.get(town))) {
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


    public boolean areEnemies(Player a , Player b){
        String aTeam = getTeam(a.getUniqueId().toString());
        String bTeam = getTeam(b.getUniqueId().toString());
        if(Objects.equals(aTeam,bTeam)){
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

    public String getEmpirePrefix(String empire){
        String empirePrefix;
        if(Objects.equals(empire.toUpperCase(),"OTTOMANS")){
            empirePrefix = ChatColor.YELLOW + "[Ottoman]";
            return  empirePrefix;
        }else if(Objects.equals(empire.toUpperCase(),"MONGOLS")){
            empirePrefix = ChatColor.DARK_BLUE + "[Mongol]";
            return  empirePrefix;
        }else if(Objects.equals(empire.toUpperCase(),"ROMANS")){
            empirePrefix = ChatColor.DARK_RED + "[Roman]";
            return empirePrefix;
        }else{
            empirePrefix = ChatColor.DARK_PURPLE + "[Viking]";
            return empirePrefix;
        }
    }

    public StatHandler getStatHandler(){
        return statHandler;
    }
    public String getPlayerGroup(Player player){
        User user =  api.getUserManager().getUser(player.getUniqueId());
        QueryOptions queryOptions = api.getContextManager().getQueryOptions(player);
        CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);
        String group = metaData.getPrimaryGroup();
        return group;

    }
    public void warpTown(Player player , String town){
        if(TownSpawns.containsKey(town.toUpperCase())){
            if(Objects.equals(getTeam(player.getUniqueId().toString()),CaptureOwners.get(town.toUpperCase()))) {
                if(!playerTeleport.containsKey(player.getUniqueId())) {
                    Random random = new Random();
                    double rand = random.nextInt(6);
                    Location loca = TownSpawns.get(town.toUpperCase());
                    Location locb = new Location(loca.getWorld(), loca.getX(), loca.getY(), loca.getZ());
                    locb.add(rand, 0, rand);
                    int delay = 10; //delay in seconds
                    player.sendMessage(ChatColor.GOLD + "Teleporting to " + town + " in 10 seconds. Don't move!");

                    playerTeleport.put(player.getUniqueId(), false); //map storing thier id to whether they moved

                    Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            if (!playerTeleport.get(player.getUniqueId())) { //if thy havnt moved
                                player.teleport(locb);// teleports them to the location
                                playerTeleport.remove(player.getUniqueId());// remove them from the map
                            } else {
                                player.sendMessage(ChatColor.RED + "You moved!");
                                playerTeleport.remove(player.getUniqueId());// remove them from the map
                            }
                        }
                    }, 20 * delay);
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



    public void changeTownColor(String town , String owner){
        int color;
        ChatColor chatColor;

        if(Objects.equals(owner,"OTTOMANS")){
            color = OTTOMAN_MAPCOLOR;
            chatColor = OTTOMAN_CHATCOLOR;
        }else if(Objects.equals(owner,"MONGOLS")){
            color = MONGOL_MAPCOLOR;
            chatColor = MONGOL_CHATCOLOR;
        }else if(Objects.equals(owner,"ROMANS")){
            color = ROMAN_MAPCOLOR;
            chatColor = ROMAN_CHATCOLOR;
        }else if(Objects.equals(owner,"VIKINGS")){
            color = VIKING_MAPCOLOR;
            chatColor = VIKING_CHATCOLOR;
        }else{
            color = 0xFFFFFF;
            chatColor = ChatColor.WHITE;
        }


        Collection<AreaMarker> set = dynmapCommonAPI.getMarkerAPI().getMarkerSet("markers").getAreaMarkers();
        for(AreaMarker areaMarker : set){
            String l = areaMarker.getLabel();
            if(Objects.equals(l.toUpperCase(),town)){
                areaMarker.setLineStyle(3, 1.0, color);
                areaMarker.setFillStyle(0.35,color);
                areaMarker.setGreetingText(chatColor + town,TownType.get(town.toUpperCase()) + " town");


            }
        }

    }






}
