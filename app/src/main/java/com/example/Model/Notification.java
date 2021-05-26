package com.example.Model;

public class Notification {
    String postID;
    int toUserType;   // 2:follow and tao,  1 : ng tao , 0 : ng follow
    String userID;
    String avtResource;
    String username;
    String action;
    String toUser;
    String time;
    boolean readed;
    boolean isNew;

    public Notification() {
        super();
    }

    public Notification(String postID, int toUserType, String userID, String avtResource, String username, String action, String toUser, boolean readed, String time) {
        this.postID = postID;
        this.toUserType= toUserType;
        this.userID = userID;
        this.avtResource = avtResource;
        this.username = username;
        this.action = action;
        this.toUser = toUser;
        this.readed = readed;
        this.time= time;
        this.isNew= true;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public int getToUserType() {
        return toUserType;
    }

    public void setToUserType(int toUserType) {
        this.toUserType = toUserType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAvtResource() {
        return avtResource;
    }

    public void setAvtResource(String avtResource) {
        this.avtResource = avtResource;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
