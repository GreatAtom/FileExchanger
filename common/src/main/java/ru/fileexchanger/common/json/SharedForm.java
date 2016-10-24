package ru.fileexchanger.common.json;

import java.util.List;

/**
 * Created by Dmitry on 24.10.2016.
 */
public class SharedForm {
    private List<Integer> filesIds;
    private List<String> logins;

    public List<Integer> getFilesIds() {
        return filesIds;
    }

    public void setFilesIds(List<Integer> filesIds) {
        this.filesIds = filesIds;
    }

    public List<String> getLogins() {
        return logins;
    }

    public void setLogins(List<String> logins) {
        this.logins = logins;
    }
}
