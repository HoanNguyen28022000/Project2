package com.example.Model;

public class Follow {
    String userID;
    String postID;

    public Follow() {
        super();
    }

    public Follow(String userID, String postID) {
        this.postID = postID;
        this.userID=userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }
}
