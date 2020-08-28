package com.tort.EmpireBattles;

import com.tort.EmpireBattles.Commands.Empires;

import com.tort.EmpireBattles.Commands.Towns;
import com.tort.EmpireBattles.Events.PlayerJoinEvents;
import com.tort.EmpireBattles.Events.PlayerRespawnEvents;
import com.tort.EmpireBattles.Files.EmpireDataManager;
import com.tort.EmpireBattles.Files.PlayerDataManager;

import com.tort.EmpireBattles.Files.TownDataManager;
import com.tort.EmpireBattles.Game.CapZone;
import com.tort.EmpireBattles.Game.GameInstance;
import com.tort.EmpireBattles.Items.CaptureTool;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;



public class Main extends JavaPlugin implements Listener {


    public Inventory inv;
    public GameInstance gameInstance;
    public PlayerDataManager playerdata;
    public EmpireDataManager empiredata;
    public TownDataManager towndata;
    public static Map<String, String> CaptureBlocks = new HashMap<String, String>();

    public static Map<String,String> playerTeams = new ConcurrentHashMap<String,String>();
    public static Map<String, Location> CaptureZones = new HashMap<String,Location>();
    public static Map<String, Location> EmpireZones = new HashMap<String,Location>();
    public static Map<String, String> CaptureOwners = new HashMap<String,String>();
    public static Map<String, Location> EmpireSpawns = new HashMap<String, Location>();
    public static Map<String, Boolean> EmpireStatus = new HashMap<String, Boolean>();

    public Map<String, Location> TownSpawns = new HashMap<String, Location>();
    public Map<String,Long> cooldowns = new HashMap<String,Long>();




    @Override
    public void onEnable() {
        //Register Events
        getServer().getPluginManager().registerEvents(new PlayerJoinEvents(), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnEvents(this),this);
        this.getServer().getPluginManager().registerEvents(this,this);
        this.saveDefaultConfig(); // create conf.yml

        //Data
        this.playerdata = new PlayerDataManager(this);
        this.empiredata = new EmpireDataManager(this);
        this.towndata = new TownDataManager(this);

        //set Executor for commands
        Towns command = new Towns(this);


        //Set Empire Commands

        Empires empires = new Empires(this);
        getCommand("empire").setExecutor(empires);
        getCommand("setspawn").setExecutor(empires);
        getCommand("spawn").setExecutor(empires);



        //Log enabled Plugin
        getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "[Empire Battles]: Plugin is enabled");



        //Item Init
        CaptureTool.init();

        //Restore Hashmaps
        this.restoreCbs();





    }

    @Override
    public void onDisable(){

        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Empire Battles]: Plugin is disabled");
            this.saveCbs();

    }

    // Cbs = Capture Blocks
    public void saveCbs(){
        for(Map.Entry<String,String> entry: CaptureBlocks.entrySet()){
            this.getConfig().set("data." + entry.getKey(), entry.getValue());
        }

        for(Map.Entry<String,Location> entry: CaptureZones.entrySet()){
            double X = entry.getValue().getX();
            double Y = entry.getValue().getY();
            double Z = entry.getValue().getZ();
            this.getConfig().set("Zones." + entry.getKey().toUpperCase() + ".X", X );
            this.getConfig().set("Zones." + entry.getKey().toUpperCase() + ".Y", Y );
            this.getConfig().set("Zones." + entry.getKey().toUpperCase() + ".Z", Z );
            this.getConfig().set("Zones." + entry.getKey().toUpperCase() + ".zoneowner", CaptureOwners.get(entry.getKey()));
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


            Location capLocation = new Location(Bukkit.getServer().getWorld("world"),X,Y,Z);
            CaptureZones.put(zone,capLocation);
            getLogger().log(Level.INFO, "Put " + zone);
            CaptureOwners.put(zone,zoneowner);

            Location spawnLocation = new Location(Bukkit.getServer().getWorld("world"),spawnX,spawnY,spawnZ);
            TownSpawns.put(zone, spawnLocation);
        }

        Collection<String> Empires = this.empiredata.getConfig().getKeys(false);
            for(String empire: Empires){
                Double X = empiredata.getConfig().getDouble(empire + ".spawnpoint" + ".X");
                Double Y = empiredata.getConfig().getDouble(empire + ".spawnpoint" + ".Y");
                Double Z = empiredata.getConfig().getDouble(empire + ".spawnpoint" + ".Z");
                Float yaw = Float.valueOf(empiredata.getConfig().getString(empire + ".spawnpoint" + ".yaw"));
                Float pitch = Float.valueOf(empiredata.getConfig().getString(empire + ".spawnpoint" + ".pitch"));
                double capX =  this.empiredata.getConfig().getDouble(empire+ ".capture"+".X");
                double capY = this.empiredata.getConfig().getDouble(empire+ ".capture"+".Y");
                double capZ = this.empiredata.getConfig().getDouble(empire+ ".capture"+".Z");
                Boolean status = this.empiredata.getConfig().getBoolean(empire + ".status");

                Location location = new Location(Bukkit.getServer().getWorld("world"),X,Y,Z,yaw,pitch);
                Location capLocation = new Location(Bukkit.getServer().getWorld("world"),capX,capY,capZ);
                EmpireSpawns.put(empire,location);
                EmpireZones.put(empire,capLocation);
                EmpireStatus.put(empire, status);

            }
    }

     public static String getTeam(String playerID){ //get playerID from hashmap
        return playerTeams.get(playerID);
    }
    public static Boolean getStatus(String empire){ //get Empire status
        return EmpireStatus.get(empire.toUpperCase());
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
    public void onPlayerJoin(PlayerJoinEvent event) {

        createBoard(event.getPlayer());

        if(playerdata.getConfig().contains("players."+ event.getPlayer().getUniqueId().toString() + ".empire") || Main.playerTeams.containsKey(event.getPlayer().getUniqueId().toString()) ) {

            if (!Main.playerTeams.containsKey(event.getPlayer().getUniqueId().toString())) {
                playerTeams.put(event.getPlayer().getUniqueId().toString(), playerdata.getConfig().getString("players." + event.getPlayer().getUniqueId().toString() + ".empire"));
            }
            String playerEmpire = playerTeams.get(event.getPlayer().getUniqueId().toString());
            if (Objects.equals(EmpireStatus.get(playerEmpire), false)) {
                if (Objects.equals(playerTeams.get(event.getPlayer().getUniqueId().toString()), "MONGOLS")) {
                    event.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.DARK_BLUE + "MONGOL" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the game.");
                    event.getPlayer().setPlayerListName(ChatColor.DARK_BLUE + "[Mongol] " + ChatColor.WHITE + event.getPlayer().getName());
                    event.getPlayer().setDisplayName(ChatColor.DARK_BLUE + event.getPlayer().getName());


                } else if (Objects.equals(playerTeams.get(event.getPlayer().getUniqueId().toString()), "OTTOMANS")) {
                    event.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "OTTOMAN" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the game. ");
                    event.getPlayer().setPlayerListName(ChatColor.YELLOW + "[Ottoman] " + ChatColor.WHITE + event.getPlayer().getName());
                    event.getPlayer().setDisplayName(ChatColor.YELLOW + event.getPlayer().getName());

                } else if (Objects.equals(playerTeams.get(event.getPlayer().getUniqueId().toString()), "ROMANS")) {
                    event.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.DARK_RED + "ROMAN" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the game. ");
                    event.getPlayer().setPlayerListName(ChatColor.DARK_RED + "[Roman] " + ChatColor.WHITE + event.getPlayer().getName());
                    event.getPlayer().setDisplayName(ChatColor.DARK_RED + event.getPlayer().getName());

                } else {
                    event.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "VIKING" + ChatColor.GRAY + "] " + ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the game. ");
                    event.getPlayer().setPlayerListName(ChatColor.DARK_PURPLE + "[Viking] " + ChatColor.WHITE + event.getPlayer().getName());
                    event.getPlayer().setDisplayName(ChatColor.DARK_PURPLE + event.getPlayer().getName());
                }

            }else{
                Main.setTeam(event.getPlayer(),"NEUTRAL");
                event.getPlayer().getInventory().clear();
                event.getPlayer().teleport(Bukkit.getServer().getWorld("world").getSpawnLocation());
                event.getPlayer().sendMessage(ChatColor.RED + "Your empire has been captured. Choose another Empire!");
            }


            } else {
                event.getPlayer().teleport(getServer().getWorld("world").getSpawnLocation());
                event.setJoinMessage(ChatColor.YELLOW + event.getPlayer().getDisplayName() + " has joined the game.");
            }



    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event){
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(event.getItem() != null){
                if(event.getItem().getItemMeta().equals(CaptureTool.CaptureTool.getItemMeta())){
                             Double location1X =   event.getClickedBlock().getLocation().getX();
                             Double location1Y =   event.getClickedBlock().getLocation().getY();
                             Double location1Z =   event.getClickedBlock().getLocation().getZ();
                            towndata.getConfig().set("Location1X",location1X);
                            towndata.getConfig().set("Location1Y",location1Y);
                            towndata.getConfig().set("Location1Z",location1Z);
                            towndata.saveConfig();
                            event.getPlayer().sendMessage("Location 1 set to:" + location1X + "," + location1Y + "," + location1Z);
                }
            }

        }    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event){
        if(event.getAction() == Action.LEFT_CLICK_BLOCK){
            if(event.getItem() != null){
                if(event.getItem().getItemMeta().equals(CaptureTool.CaptureTool.getItemMeta())){


                    Double location2X =   event.getClickedBlock().getLocation().getX();
                    Double location2Y =   event.getClickedBlock().getLocation().getY();
                    Double location2Z =   event.getClickedBlock().getLocation().getZ();
                    towndata.getConfig().set("Location2X",location2X);
                    towndata.getConfig().set("Location2Y",location2Y);
                    towndata.getConfig().set("Location2Z",location2Z);
                    towndata.saveConfig();
                    event.getPlayer().sendMessage("Location 2 set to:" + location2X + "," + location2Y + "," + location2Z);

                }
            }

        }    }

    @EventHandler
    public void onPlayerSay(AsyncPlayerChatEvent event){

        if(playerdata.getConfig().get("players."+ event.getPlayer().getUniqueId().toString() + ".empire").equals("MONGOLS")){
            event.setFormat(ChatColor.GRAY + "[" + ChatColor.DARK_BLUE + "MONGOL" + ChatColor.GRAY + "] " + ChatColor.WHITE + event.getPlayer().getDisplayName() + ": "+ event.getMessage());
        }

        if(playerdata.getConfig().get("players."+ event.getPlayer().getUniqueId().toString() + ".empire").equals("OTTOMANS")){
            event.setFormat(ChatColor.GRAY + "[" + ChatColor.YELLOW + "OTTOMAN" + ChatColor.GRAY + "] " + ChatColor.WHITE + event.getPlayer().getDisplayName() + ": "+ event.getMessage());
        }

        if(playerdata.getConfig().get("players."+ event.getPlayer().getUniqueId().toString() + ".empire").equals("ROMANS")){
            event.setFormat(ChatColor.GRAY + "[" + ChatColor.DARK_RED + "ROMAN" + ChatColor.GRAY + "] " + ChatColor.WHITE + event.getPlayer().getDisplayName() + ": "+ event.getMessage());

        }

        if(playerdata.getConfig().get("players."+ event.getPlayer().getUniqueId().toString() + ".empire").equals("VIKINGS")){
            event.setFormat(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "VIKING" + ChatColor.GRAY + "] " + ChatColor.WHITE + event.getPlayer().getDisplayName() + ": "+ event.getMessage());
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] strings) {
        Player player = (Player) sender;

        //Capture tool
        if(command.getName().equalsIgnoreCase("capturetool")){
                player.getInventory().addItem(CaptureTool.CaptureTool);
        }

        if(command.getName().equalsIgnoreCase("warp")){
            if(TownSpawns.containsKey(strings[0].toUpperCase())){
                if(Objects.equals(getTeam(player.getUniqueId().toString()),CaptureOwners.get(strings[0].toUpperCase()))) {
                    Random random = new Random();
                    double rand = random.nextInt(6);
                    Location loca = TownSpawns.get(strings[0].toUpperCase());
                    Location locb = new Location(loca.getWorld(), loca.getX(), loca.getY(), loca.getZ());
                    locb.add(rand, 0, rand);
                    player.sendMessage(ChatColor.GOLD + "Teleporting to " + strings[0] + " in 5 seconds.");
                    Bukkit.getScheduler().runTaskLater(this, () -> player.teleport(locb.add(rand, 0, rand)), 5 * 20);

                }else{
                    player.sendMessage(ChatColor.RED + " Your team does not own this town.");
                }
            }else{
                player.sendMessage(ChatColor.RED + " Invalid town name.");
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

        }

        if(command.getName().equalsIgnoreCase("start")) {
            gameInstance = new GameInstance(this);
            gameInstance.start();
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
        if (block.getType() == Material.IRON_ORE) { // Checks if the block broken is Iron Ore.
            block.setType(Material.AIR); // If true, replaces Iron Ore with Air block. (Removes block.)
            inv.addItem(iron); // Adds iron bar to player's inventory.
            Bukkit.getScheduler().runTaskLater(this, () -> block.setType(Material.IRON_ORE), 100);
            //inv.addItem(new ItemStack[] { iron }); // Adds iron bar to player's inventory.
            return;
        }
        if (block.getType() == Material.GOLD_ORE) { // Checks if the block broken is Gold Ore.
            block.setType(Material.AIR); // If true, replaces Gold Ore block with Air block. (Removes block.)
            inv.addItem(gold);
            Bukkit.getScheduler().runTaskLater(this, () -> block.setType(Material.GOLD_ORE), 100);
            return;
        }
        if (block.getType() == Material.ANCIENT_DEBRIS) { // Checks if the block broken is Iron Ore.
            block.setType(Material.AIR); // If true, replaces Iron Ore with Air block. (Removes block.)
            inv.addItem(netherite); // Adds iron bar to player's inventory.
            Bukkit.getScheduler().runTaskLater(this, () -> block.setType(Material.ANCIENT_DEBRIS), 100);
            //inv.addItem(new ItemStack[] { iron }); // Adds iron bar to player's inventory.
            return;
        }
            if (block.getType() == Material.DIAMOND_ORE) { // Checks if the block broken is Gold Ore.
                block.setType(Material.AIR); // If true, replaces Gold Ore block with Air block. (Removes block.)
                inv.addItem(diamond);
                Bukkit.getScheduler().runTaskLater(this, () -> block.setType(Material.DIAMOND_ORE), 100);
                return;
            }

    }


    public void createBoard(Player player){
         ScoreboardManager manager = Bukkit.getScoreboardManager();
         Scoreboard board = manager.getNewScoreboard();
         Objective obj = board.registerNewObjective("Scoreboard-1","dummy","Players Online");
         Team OTTOMANS = board.registerNewTeam("OTTOMANS");
         Team MONGOLS = board.registerNewTeam("MONGOLS");
         Team ROMANS =  board.registerNewTeam("ROMANS");
         Team VIKINGS =  board.registerNewTeam("VIKINGS");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        OTTOMANS.setColor(ChatColor.YELLOW);
        OTTOMANS.setPrefix(ChatColor.YELLOW + "[Ottoman]" + ChatColor.WHITE);

        MONGOLS.setColor(ChatColor.DARK_BLUE);
        MONGOLS.setPrefix(ChatColor.DARK_BLUE+ "[Mongol]" + ChatColor.WHITE);

        ROMANS.setColor(ChatColor.DARK_RED);
        ROMANS.setPrefix(ChatColor.DARK_RED+ "[Roman]" + ChatColor.WHITE);

        VIKINGS.setColor(ChatColor.DARK_PURPLE);
        VIKINGS.setPrefix(ChatColor.DARK_PURPLE+ "[Viking]" + ChatColor.WHITE);


        OTTOMANS.setAllowFriendlyFire(false);
        MONGOLS.setAllowFriendlyFire(false);
        ROMANS.setAllowFriendlyFire(false);
        VIKINGS.setAllowFriendlyFire(false);

        if(playerdata.getConfig().contains("players."+ player.getUniqueId().toString() + ".empire")){
             board.getTeam(playerdata.getConfig().get("players."+ player.getUniqueId().toString() + ".empire").toString()).addEntry(player.getName());


        }

        Score score = obj.getScore(ChatColor.BLACK + "=-=-=-=-=-=-=-=-=-=");
        score.setScore(4);

        Score score2 = obj.getScore(ChatColor.YELLOW + "OTTOMANS:    " + OTTOMANS.getEntries().size() );
        score2.setScore(3);

        Score score3 = obj.getScore(ChatColor.DARK_BLUE + "MONGOLS:    " + MONGOLS.getEntries().size());
        score3.setScore(2);

        Score score4 = obj.getScore(ChatColor.DARK_RED + "ROMANS:      " + ROMANS.getEntries().size());
        score4.setScore(1);

        Score score5 = obj.getScore(ChatColor.DARK_PURPLE + "VIKINGS:     " + VIKINGS.getEntries().size() );
        score5.setScore(0);

        player.setScoreboard(board);








    }





}
