package main.model;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static main.model.socket.Server.DEFAULT_FILES_PATH;

/**
 * Created by Anton on 02.10.2016.
 * Client info
 */
public class Client {
    private String mLogin;
    private String mPassword;
    private String mToken;
    private Dir dir;

    private Client(String login, String pass, String token) {
        mLogin = login;
        mPassword = pass;
        mToken = token;
        dir = new Dir(token);
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

    public static Client tryCreateClient(String login, String pass) {
        login = login;
        pass = pass;

        if (valideLoginOrPass(login) && valideLoginOrPass(pass)) {
            MessageDigest md = null;
            try {
                String prepToken = login + pass + "SALT";
                byte[] prepTokenBytes = prepToken.getBytes("UTF-8");

                md = MessageDigest.getInstance("MD5");

                byte[] token = md.digest(prepTokenBytes);
                BigInteger bigInt = new BigInteger(1, token);
                String hashToken = bigInt.toString(16);
                // Now we need to zero pad it if you actually want the full 32 chars.
                while (hashToken.length() < 32) {
                    hashToken = "0" + hashToken;
                }

                return new Client(login, pass, hashToken);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
