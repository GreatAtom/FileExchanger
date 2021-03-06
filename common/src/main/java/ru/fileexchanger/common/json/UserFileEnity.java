package ru.fileexchanger.common.json;


/**
 * Created by Dmitry on 14.10.2016.
 */
public class UserFileEnity {
    private int id;
    private String userLogin;
    private String fileName;
    private long fileSize;
    private long downloadSize;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public Object[] toArray(boolean withId) {
        String status = FileStatus.UNDEFINED.name();
        if (downloadSize == fileSize) {
            status = FileStatus.DOWNLOADED.name();
        }
        if (downloadSize < fileSize) {
            status = FileStatus.NOT_ALL.name();
        }
        if (withId) {
            return new Object[]{id, fileName, String.valueOf(fileSize), String.valueOf(downloadSize), status};
        } else {
            return new Object[]{fileName, String.valueOf(fileSize), String.valueOf(downloadSize), status};
        }
    }

    public enum FileStatus {
        UNDEFINED, DOWNLOADED, NOT_ALL
    }
}
