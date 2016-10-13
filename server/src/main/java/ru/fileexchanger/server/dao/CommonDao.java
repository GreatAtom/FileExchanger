package ru.fileexchanger.server.dao;

import java.sql.*;

/**
 * Created by Dmitry on 13.10.2016.
 */
public class CommonDao {
    private static String dbURL = "jdbc:derby://localhost:1527/fileexchanger;create=true";
    private static String tableName = "restaurants";
    // jdbc Connection
    private static Connection connection = null;


    public CommonDao() {
        createConnection();
    }

    public boolean isValidUser(String login, String password){
        try {
            PreparedStatement statement = connection.prepareStatement("select count(*) c from users where login = ? and password = ?");
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count==1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void createConnection()
    {
        try
        {
            System.setProperty("derby.system.home", System.getProperty("user.home")+ "/fileexchanger");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            //Get a connection
            connection = DriverManager.getConnection(dbURL);
        }
        catch (Exception except)
        {
            except.printStackTrace();
        }
    }
}
