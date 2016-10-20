package ru.fileexchanger.common;

import java.util.List;

/**
 * Created by Dmitry on 17.10.2016.
 */
public class UserInfo {

    private List<UserFileEnity> fileEnityList;

    public List<UserFileEnity> getFileEnityList() {
        return fileEnityList;
    }

    public void setFileEnityList(List<UserFileEnity> fileEnityList) {
        this.fileEnityList = fileEnityList;
    }
}
