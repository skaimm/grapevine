package com.grapevine.grapevine.Models;

import com.mysql.jdbc.Blob;

import java.util.ArrayList;

public class Tree {

    int treeid;
    Blob timage;
    ArrayList<Branch> branches;
    ArrayList<Review> reviewList;

    public Tree(int treeid, Blob timage, ArrayList<Branch> branches, ArrayList<Review> reviewList) {
        this.treeid = treeid;
        this.timage = timage;
        this.branches = branches;
        this.reviewList = reviewList;
    }

    public  Tree(){

    }

    public int getTreeid() {
        return treeid;
    }

    public void setTreeid(int treeid) {
        this.treeid = treeid;
    }

    public Blob getTimage() {
        return timage;
    }

    public void setTimage(Blob timage) {
        this.timage = timage;
    }

    public ArrayList<Branch> getBranches() {
        return branches;
    }

    public void setBranches(ArrayList<Branch> branches) {
        this.branches = branches;
    }

    public ArrayList<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(ArrayList<Review> reviewList) {
        this.reviewList = reviewList;
    }
}
