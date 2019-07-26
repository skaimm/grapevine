package com.grapevine.grapevine.Models;

public class Review {

    int userid;
    String comment;
    int importance;
    int treeid;

    public Review(int userid, String comment, int importance, int treeid) {
        this.userid = userid;
        this.comment = comment;
        this.importance = importance;
        this.treeid = treeid;
    }
    public Review(int userid,int treeid){
        this.userid = userid;
        this.treeid = treeid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public int getTreeid() {
        return treeid;
    }

    public void setTreeid(int treeid) {
        this.treeid = treeid;
    }
}
