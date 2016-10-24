package ru.fileexchanger.server.dao;

import org.omg.CORBA.PUBLIC_MEMBER;
import ru.fileexchanger.common.json.UserFileEnity;

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

    public void makeQuery(String query) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.executeUpdate();
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
        PreparedStatement preparedStatement = connection.prepareStatement("select fileId, userLogin, fileName, fileSize from USERFILES where userLogin=?");
        preparedStatement.setString(1, userLogin);
        ResultSet resultSet = preparedStatement.executeQuery();
        return fillUserFileEnitys(resultSet);
    }

    public List<UserFileEnity> loadSharedFilesForUsers(String userLogin) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select fileId, userLogin, fileName, fileSize from USERFILES where fileId in (select fileId from shared where userLogin = ?)");
        preparedStatement.setString(1, userLogin);
        ResultSet resultSet = preparedStatement.executeQuery();
        return fillUserFileEnitys(resultSet);
    }

    private List<UserFileEnity> fillUserFileEnitys(ResultSet resultSet) throws SQLException {
        List<UserFileEnity> files = new ArrayList<UserFileEnity>();
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

    /**
     * Возвращает список логинов всех пользователей, кроме пользовтеля с логином login
     * @param login
     * @return
     */
    public List<String> loadUsers(String login) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select login from users where login !=?");
        preparedStatement.setString(1, login);
        ResultSet resultSet =  preparedStatement.executeQuery();
        List<String> usersLogin= new ArrayList<>();
        while (resultSet.next()) {
            usersLogin.add(resultSet.getString(1));
        }
        return usersLogin;
    }

    public void insertUser(String login, String password) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("insert into users values(?, ?)");
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);
        preparedStatement.executeUpdate();
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
            connection.createStatement().execute("delete from shared");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sharedFiles(List<Integer> filesIds, List<String> logins) {
        filesIds.stream().forEach(fileId->{
            logins.stream().forEach(login->{
                try {
                    sharedFile(fileId, login);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void sharedFile(Integer fileId, String login) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("insert into shared values(?, ?)");
        preparedStatement.setString(1, login);
        preparedStatement.setInt(2, fileId);
        preparedStatement.executeUpdate();
        System.out.println("File with id='"+fileId+"' has share for '"+login+"'");
    }


    public void makePrivate(List<Integer> filesIds) {
        filesIds.stream().forEach(id -> {
            try {
                makePrivate(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void makePrivate(Integer id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("delete from shared where fileId=?");
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("File with id='"+id+"' is private");
    }
}
