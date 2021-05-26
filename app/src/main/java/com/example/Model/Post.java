package com.example.Model;

import java.util.ArrayList;

public class Post {
    private String postID;
    private String fromUser;
    private String avtResource;
    private String username;
    private String timePosted;
    private long price;
    private String itemName;
    private String itemType;
    private boolean status;
    private String detail;
    private String imgItemResource;
    private int countCmt;
    private int countFollows;
    private boolean isFollowed;

    private ArrayList<Comment> listCmt;

    public Post() {
        super();
    }

    public Post(String fromUser, String avtResource, String username, String timePosted, long price, String itemName, String itemType, boolean status, String detail, int countFollows, int countCmt) {
        this.fromUser = fromUser;
        this.avtResource= avtResource;
        this.username= username;
        this.timePosted = timePosted;
        this.price = price;
        this.itemName = itemName;
        this.itemType= itemType;
        this.status = status;
        this.detail = detail;
        this.countFollows = countFollows;
        this.countCmt= countCmt;
    }


    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID=postID;
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

    public String getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(String timePosted) {
        this.timePosted = timePosted;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImgItemResource() {
        return imgItemResource;
    }

    public void setImgItemResource(String imgItemResource) {
        this.imgItemResource = imgItemResource;
    }

    public int getCountFollows() {
        return countFollows;
    }

    public void setCountFollows(int countFollows) {
        this.countFollows = countFollows;
    }

    public int getCountCmt() {
        return countCmt;
    }

    public void setCountCmt(int countCmt) {
        this.countCmt = countCmt;
    }

    public ArrayList<Comment> getListCmt() {
        return listCmt;
    }

    public void setListCmt(ArrayList<Comment> listCmt) {
        this.listCmt = listCmt;
    }
}
