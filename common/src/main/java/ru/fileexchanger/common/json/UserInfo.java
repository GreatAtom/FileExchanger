package ru.fileexchanger.common.json;

import java.util.List;

/**
 * Created by Dmitry on 17.10.2016.
 */
public class UserInfo {

    private List<UserFileEnity> fileEnityList;

    private List<String> users;

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<UserFileEnity> getFileEnityList() {
        return fileEnityList;
    }

    public void setFileEnityList(List<UserFileEnity> fileEnityList) {
        this.fileEnityList = fileEnityList;
    }
}
