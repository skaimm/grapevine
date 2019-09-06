package com.grapevine.grapevine.Models;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;

import com.grapevine.grapevine.Models.Tree;

import java.util.ArrayList;
import java.util.Objects;


@SuppressLint("ParcelCreator")
public class Branch {


    int id;
    int parentid;
    int treeid;
    ArrayList<Coordinates> coordinates;
    ArrayList<Integer> children;

    public Branch(int id, int parentid, int treeid, ArrayList<Coordinates> coordinates) {
        this.id = id;
        this.parentid = parentid;
        this.treeid = treeid;
        this.coordinates = coordinates;
        this.children = new ArrayList<>();
    }

    public Branch(int id){
        this.id = id;
        this.children = new ArrayList<>();
        this.coordinates = new ArrayList<>();
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public int getTreeid() {
        return treeid;
    }

    public void setTreeid(int treeid) {
        this.treeid = treeid;
    }

    public ArrayList<Coordinates> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates.add(coordinates);
    }

    public ArrayList<Integer> getChildren() {
        return children;
    }

    public void setChildren(Integer children) {
        this.children.add(children);
    }

    public void setChildrenasArray(ArrayList<Integer> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "id=" + id +
                ", parentid=" + parentid +
                ", treeid=" + treeid +
                ", coordinates=" + coordinates +
                ", children=" + children +
                '}';
    }
}
