package com.grapevine.grapevine.Models;

public class Pruning {

    int x;
    int y;
    int treeid;

    public Pruning(int x, int y, int treeid, Person personid) {
        this.x = x;
        this.y = y;
        this.treeid = treeid;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getTreeid() {
        return treeid;
    }

    public void setTreeid(int treeid) {
        this.treeid = treeid;
    }
}
