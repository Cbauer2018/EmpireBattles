package com.tort.EmpireBattles.NPCS;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.tort.EmpireBattles.Files.EmpireUtils;
import com.tort.EmpireBattles.Main;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NPC {


    private   List<EntityPlayer> NPC = new ArrayList<>();
    private Main plugin;

    public NPC(Main plugin){
        this.plugin = plugin;
    }


    public  void createNPC(Main plugin, Player player, String skin){
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) Bukkit.getWorld(player.getWorld().getName())).getHandle();

        UUID uuid = UUID.randomUUID();
        GameProfile gameProfile = new GameProfile(uuid, ChatColor.GREEN + "" + ChatColor.BOLD + "Store");
        EntityPlayer npc = new EntityPlayer(server,world,gameProfile,new PlayerInteractManager(world));
        npc.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        String[] name = getSkin(player,skin);
        gameProfile.getProperties().put("textures", new Property("textures",name[0],name[1]));

        addNPCPacket(npc , plugin);
        NPC.add(npc);
        saveNPC(plugin, player , name, "Store" , uuid.toString());

    }

    public  void addNPCPacket(EntityPlayer npc , Main plugin){
        for(Player player : Bukkit.getOnlinePlayers()){
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256/360)));


            DataWatcher watcher = npc.getDataWatcher();
            watcher.set(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 255);
            connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), watcher, true));

            Entity e = (Entity) npc;


            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    removeFromTablist(player,npc);
                }
            }, 30);

        }
    }

    private  String[] getSkin(Player player, String name){
        try{
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid
                    + "?unsigned=false");

            InputStreamReader reader2 = new InputStreamReader(url2.openStream());
            JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture= property.get("value").getAsString();
            String signature = property.get("signature").getAsString();
            return new String[] {texture,signature};

        }catch (Exception e){
                EntityPlayer p = ((CraftPlayer) player).getHandle();
                GameProfile profile = p.getProfile();
                Property  property = profile.getProperties().get("textures").iterator().next();
                String texture = property.getValue();
                String signature = property.getSignature();
                return new String[] {texture,signature};
        }


    }

    public  void addJoinPacket(Player player , Main plugin){
       try {
           for (EntityPlayer npc : NPC) {
               PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
               connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
               connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
               connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));


               DataWatcher watcher = npc.getDataWatcher();
               watcher.set(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 255);
               connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), watcher, true));
               Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                   @Override
                   public void run() {
                       removeFromTablist(player,npc);
                   }
               }, 30);


           }

       }catch (Exception e){
           Bukkit.getLogger().severe(e.getMessage());

       }


    }


    public  void addSingleNPCPackets(Player player , EntityPlayer npc){
        try {

                PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
                connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
                connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));


                DataWatcher watcher = npc.getDataWatcher();
                watcher.set(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 255);
                connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), watcher, true));
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                    @Override
                    public void run() {
                        removeFromTablist(player,npc);
                    }
                }, 30);

        }catch (Exception e){
            Bukkit.getLogger().severe(e.getMessage());

        }


    }

    public  List<EntityPlayer> getNPCs(){
        return NPC;
    }

    public void saveNPC(com.tort.EmpireBattles.Main plugin , Player player , String[] skin, String name, String uuid ){
        int var = 1;
        if(plugin.npcdata.getConfig().contains("data")){
            var = plugin.npcdata.getConfig().getConfigurationSection("data").getKeys(false).size() + 1;
        }

        plugin.npcdata.getConfig().set("data." + var + ".x",(int) player.getLocation().getX());
        plugin.npcdata.getConfig().set("data." + var + ".y",(int) player.getLocation().getY());
        plugin.npcdata.getConfig().set("data." + var + ".z",(int) player.getLocation().getZ());
        plugin.npcdata.getConfig().set("data." + var + ".pitch", player.getLocation().getPitch());
        plugin.npcdata.getConfig().set("data." + var + ".yaw", player.getLocation().getYaw());
        plugin.npcdata.getConfig().set("data." + var + ".world", player.getLocation().getWorld().getName());
        plugin.npcdata.getConfig().set("data." + var + ".name", name);
        plugin.npcdata.getConfig().set("data." + var + ".texture", skin[0]);
        plugin.npcdata.getConfig().set("data." + var + ".signature", skin[1]);
        plugin.npcdata.getConfig().set("data." + var + ".uuid", uuid);
        plugin.npcdata.saveConfig();
    }

    public void loadNPC(Location location , GameProfile profile, Main plugin){
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) Bukkit.getWorld(location.getWorld().getName())).getHandle();
        GameProfile gameProfile = profile;
        EntityPlayer npc = new EntityPlayer(server,world,gameProfile,new PlayerInteractManager(world));
        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        addNPCPacket(npc, plugin );

        NPC.add(npc);



    }
    public void removeNPC(Player player, EntityPlayer npc){
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));

    }


    public void deleteNPC(EntityPlayer npc ,Main plugin){
        EntityPlayer target = null;

        for(EntityPlayer p: NPC){
            if(Objects.equals(p.getUniqueID(), npc.getUniqueID())){
                target = p;
                break;
            }
        }
        if(!Objects.equals(target,null)) {
            NPC.remove(target);
            Collection<String> npcList = plugin.npcdata.getConfig().getConfigurationSection("data").getKeys(false);

            for(String index : npcList){
                    String uuid = plugin.npcdata.getConfig().getString("data." + index + ".uuid");
                    if(Objects.equals(uuid,target.getUniqueID().toString())){
                        plugin.npcdata.getConfig().set("data." + index , null);
                        plugin.npcdata.saveConfig();
                        break;
                    }
            }

            for(Player player : Bukkit.getOnlinePlayers()){

                PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutEntityDestroy(target.getId()));
            }

        }
    }

    public void removeFromTablist(Player player, EntityPlayer npc){
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
    }

}
