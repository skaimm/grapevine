package com.grapevine.grapevine.Models;

public class Coordinates {

    float x;
    float y;
    int brancdid;

    public Coordinates(float x, float y, int brancdid) {
        this.x = x;
        this.y = y;
        this.brancdid = brancdid;
    }

    public float getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getBrancdid() {
        return brancdid;
    }

    public void setBrancdid(int brancdid) {
        this.brancdid = brancdid;
    }
}
