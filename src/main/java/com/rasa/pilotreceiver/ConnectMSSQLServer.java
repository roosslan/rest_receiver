package com.rasa.pilotreceiver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConnectMSSQLServer
{
    static Connection conn;
    public void dbConnect(String db_connect_string,
                          String db_userid,
                          String db_password)
    {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(db_connect_string,
                    db_userid, db_password);
            System.out.println("MSSQL server connected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean insertRow(String ZVI_code, String Project, String userLogin) {
        try {
            Statement statement = conn.createStatement();
            String queryString = "insert into pilot_zvi (zvi, project, username, is_active) VALUES('" + ZVI_code + "', '" + Project + "', '" + userLogin + "', 1);";
            System.out.println(queryString);
            ResultSet rs = statement.executeQuery(queryString);
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean updateRow(String ZVI_code) {
        try {
            Statement statement = conn.createStatement();
            String queryString = "update pilot_zvi set is_active = 0 where zvi = '" + ZVI_code + "';";
            System.out.println(queryString);
            ResultSet rs = statement.executeQuery(queryString);
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
