package com.nexus.nexuschat.SQLitedatabase;

import java.sql.*;

public class SQLiteDemo {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:sqlite:/C:\\SQLite\\sqlite-tools-win-x64-3510300\\usersdb.db";

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl);
            String sql = "SELECT rowid, * FROM users";

            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery(sql);

            while(result.next()) {
                int id = result.getInt("rowid");
                String name = result.getString("name");
                String email = result.getString("email");

                System.out.println(id + " | " + name + " | " + email);
            }

        } catch (SQLException e) {
            System.out.println("Error connecting to SQLite database");
            throw new RuntimeException(e);
        }
    }
}
