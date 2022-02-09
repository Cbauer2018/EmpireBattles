package com.tort.EmpireBattles.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
    private final String port = "3306";

    private String host = "host";
    private String database = "test";
    private String username = "root";
    private String password = "";

    private Connection connection;

    public MySQL(String host, String database , String username , String password ){
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public boolean isConnected(){
        return (connection == null ? false : true);

    }

    public void connect() throws ClassNotFoundException, SQLException{
        if(!isConnected()) {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true",
                    username, password);
        }
    }


    public void disconnect(){
        if(isConnected()){
            try{
                connection.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

    }

    public Connection getConnection() {
        return connection;
    }
}
