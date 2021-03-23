package com.geekbrains.server;

import java.sql.*;

public class DataBaseSingleton implements AutoCloseable {
    private static DataBaseSingleton dataBaseSingleton;
    private static Connection connection;

    private static PreparedStatement findNicknameStatement;
    private static PreparedStatement updateNicknameStatement;

    private DataBaseSingleton() { }

    public static DataBaseSingleton getDataBaseSingleton() {
        if (dataBaseSingleton == null) {
            initConnection();
            dataBaseSingleton = new DataBaseSingleton();
        }

        return dataBaseSingleton;
    }

    private static void initConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:UsersDB.db");

            findNicknameStatement = connection.prepareStatement("SELECT nickname from users WHERE LOWER(login) = LOWER(?) AND password = ?");
            updateNicknameStatement = connection.prepareStatement("UPDATE users SET nickname = ? WHERE nickname = ?");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public String getNicknameOrNull(String login, String password) {
        ResultSet resultSet = null;

        try {
            findNicknameStatement.setString(1, login);
            findNicknameStatement.setString(2, password);

            resultSet = findNicknameStatement.executeQuery();

            return resultSet.getString("nickname");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return null;
    }

    public boolean updateNickname(String oldNickname, String newNickname) {
        int result = 0;

        try {
            updateNicknameStatement.setString(1, newNickname);
            updateNicknameStatement.setString(2, oldNickname);

            result = updateNicknameStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result != 0;
    }

    @Override
    public void close() throws Exception {
        findNicknameStatement.close();
        updateNicknameStatement.close();
        connection.close();
    }
}
