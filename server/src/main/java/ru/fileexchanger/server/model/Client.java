package ru.fileexchanger.server.model;

import ru.fileexchanger.server.dao.CommonDao;

/**
 * Created by Anton on 02.10.2016.
 * Client info
 */
public class Client {
    private String mLogin;
    private String mPassword;
    private String mToken;
    private Dir dir;
    private CommonDao commonDao;

    public Client(String login, String pass, String token) {
        mLogin = login;
        mPassword = pass;
        mToken = token;
        dir = new Dir(token);
        commonDao = new CommonDao();
    }

    private static boolean valideLoginOrPass(String str) {
        if (str != null && str.trim().length() > 5 && str.trim().length() < 11)
            return true;
        return false;
    }

    public String getmLogin() {
        return mLogin;
    }

    public String getmPassword() {
        return mPassword;
    }

    public String getmToken() {
        return mToken;
    }

    public Dir getDir() {
        return dir;
    }

}
