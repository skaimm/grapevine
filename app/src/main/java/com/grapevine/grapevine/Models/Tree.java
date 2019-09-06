package com.grapevine.grapevine.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Tree implements Serializable {

    int treeid;
    String timage;
    String cimage;
    int personid;
    ArrayList<Branch> branches;
    ArrayList<Review> reviewList;

    public Tree(int treeid, String timage, String cimage, int personid, ArrayList<Branch> branches, ArrayList<Review> reviewList) {
        this.treeid = treeid;
        this.timage = timage;
        this.cimage = cimage;
        this.personid = personid;
        this.branches = branches;
        this.reviewList = reviewList;
    }

    public Tree(int treeid, String timage, String cimage, int personid) {
        this.treeid = treeid;
        this.timage = timage;
        this.cimage = cimage;
        this.personid = personid;
        this.branches = new ArrayList<>();
        this.reviewList = new ArrayList<>();
    }

    public int getTreeid() {
        return treeid;
    }

    public void setTreeid(int treeid) {
        this.treeid = treeid;
    }

    public String getTimage() {
        return timage;
    }

    public void setTimage(String timage) {
        this.timage = timage;
    }

    public ArrayList<Branch> getBranches() {
        return branches;
    }

    public void setBranches(Branch branch) {
        this.branches.add(branch);
    }

    public ArrayList<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(ArrayList<Review> reviewList) {
        this.reviewList = reviewList;
    }

    public String getCimage() {
        return cimage;
    }

    public void setCimage(String cimage) {
        this.cimage = cimage;
    }

    public int getPersonid() {
        return personid;
    }

    public void setPersonid(int personid) {
        this.personid = personid;
    }
}
