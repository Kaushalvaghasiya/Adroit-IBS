package com.example.bmt;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {
    Connection con;
    String uname,pass, ip,port, db;
    public Connection connectionclass(){
        ip="199.79.62.22";
        uname="BMT_User";
        pass="ts214mZ$";
        db="BMT_App";
        port="1433";
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection=null;
        String conurl=null;
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conurl="jdbc:jtds:sqlserver://"+ip+":"+port+";databasename="+db+";user="+uname+";password="+pass+";";
            connection= DriverManager.getConnection(conurl);
        }
        catch (Exception e){
            Log.e("error",e.getMessage());
        }
        return connection;
    }
}
