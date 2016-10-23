package ru.fileexchanger.server.dao;

import ru.fileexchanger.common.UserFileEnity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry on 13.10.2016.
 */
public class CommonDao {
    private static String dbURL = "jdbc:derby://localhost:1527/fileexchanger;create=true";
    private static String TABLE_USER_FILES = "USERFILES";
    // jdbc Connection
    private static Connection connection = null; //one connection for all user


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

    public List<UserFileEnity> loadUserFile(String userLogin) throws SQLException {
        List<UserFileEnity> files = new ArrayList<UserFileEnity>();
        PreparedStatement preparedStatement = connection.prepareStatement("select fileId, userLogin, fileName, fileSize from USERFILES where userLogin=?");
        preparedStatement.setString(1, userLogin);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            UserFileEnity fileEnity = new UserFileEnity();
            fileEnity.setId(resultSet.getInt("fileId"));
            fileEnity.setUserLogin(resultSet.getString("userLogin"));
            fileEnity.setFileName(resultSet.getString("fileName"));
            fileEnity.setFileSize(resultSet.getLong("fileSize"));
            files.add(fileEnity);
        }
        return files;
    }

    public long insertFile(String userLogin, String fileName, long fileSize) throws SQLException {
        long id = loadMaxFileId()+1;
        PreparedStatement preparedStatement = connection.prepareStatement("insert into USERFILES values(?, ?, ?, ?)");
        preparedStatement.setLong(1, id);
        preparedStatement.setString(2, userLogin);
        preparedStatement.setString(3, fileName);
        preparedStatement.setLong(4, fileSize);
        preparedStatement.executeUpdate();
        return id;
    }

    private long loadMaxFileId() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("select max(fileId) from USERFILES");
        ResultSet rs = statement.executeQuery();
        rs.next();
        try {
            int id = rs.getInt(1);
            System.out.println("Max id: "+id);
            return id;
        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
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


    public void clearFileInfo() {
        try {
            connection.createStatement().execute("delete from USERFILES");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
