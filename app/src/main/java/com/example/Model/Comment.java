package com.example.Model;

public class Comment {
    private String commentID;
    private String fromUser;
    private String avtResource;
    private String username;
    private String postParent;
    private String message;
    private String timeSent;
    private String img_message;

    public Comment() {
        super();
    }

    public Comment(String fromUser,String avtResource, String username, String postParent, String message, String time) {
        this.fromUser = fromUser;
        this.avtResource=avtResource;
        this.username=username;
        this.postParent = postParent;
        this.message = message;
        this.timeSent = time;
    }


    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
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

    public String getPostParent() {
        return postParent;
    }

    public void setPostParent(String postParent) {
        this.postParent = postParent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }

    public String getImg_message() {
        return img_message;
    }

    public void setImg_message(String img_message) {
        this.img_message = img_message;
    }
}
