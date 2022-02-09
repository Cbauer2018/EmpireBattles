package com.tort.EmpireBattles.sql;

import com.tort.EmpireBattles.Main;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLGetter {

    private Main plugin;
    public SQLGetter(Main plugin){
        this.plugin = plugin;
    }

    public void createTable(){
        PreparedStatement ps;
        try{
            ps = plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS stats "
            + "(UUID VARCHAR(100),KILLS INT(100),DEATHS INT(100),CAPTURES INT(100),PRIMARY KEY (UUID))");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void createPlayer(Player player){
        try{
            UUID uuid = player.getUniqueId();
            if(!exists(uuid)){
                PreparedStatement ps2 = plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO stats" + " (UUID) VALUES (?)");
                ps2.setString(1,player.getUniqueId().toString());
                ps2.executeUpdate();
                ps2.close();
                return;

            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean exists(UUID uuid){
        try{
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM stats WHERE UUID=?");
            ps.setString(1, uuid.toString());

            ResultSet results = ps.executeQuery();
            if(results.next()){
                ps.close();
                results.close();
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public void addKills(UUID uuid, int kills){
        try{

            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE stats set KILLS=? WHERE UUID=?");
            ps.setInt(1, kills);
            ps.setString(2,uuid.toString());
            ps.executeUpdate();
            ps.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

    }


    public int getKills(UUID uuid){
        try{
           PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT KILLS FROM stats WHERE UUID=?");
           ps.setString(1,uuid.toString());
           ResultSet rs = ps.executeQuery();
           int points;
           if(rs.next()){
               points = rs.getInt("KILLS");
               rs.close();
               ps.close();
               return points;
           }


        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }


    public void addDeaths(UUID uuid, int deaths){
        try{

            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE stats set DEATHS=? WHERE UUID=?");
            ps.setInt(1, deaths);
            ps.setString(2,uuid.toString());
            ps.executeUpdate();
            ps.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }


    public int getDeaths(UUID uuid){
        try{
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT DEATHS FROM stats WHERE UUID=?");
            ps.setString(1,uuid.toString());
            ResultSet rs = ps.executeQuery();
            int points;
            if(rs.next()){
                points = rs.getInt("DEATHS");
                ps.close();
                rs.close();
                return points;
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }


    public void addCaptures(UUID uuid, int captures){
        try{

            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE stats set CAPTURES=? WHERE UUID=?");
            ps.setInt(1, captures);
            ps.setString(2,uuid.toString());
            ps.executeUpdate();
            ps.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }


    public int getCaptures(UUID uuid){
        try{
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT CAPTURES FROM stats WHERE UUID=?");
            ps.setString(1,uuid.toString());
            ResultSet rs = ps.executeQuery();
            int points;
            if(rs.next()){
                points = rs.getInt("CAPTURES");
                ps.close();
                rs.close();
                return points;
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

}
