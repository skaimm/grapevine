package com.grapevine.grapevine.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Coordinates {

    int x;
    int y;
    int brancdid;

    public Coordinates(int x, int y, int brancdid) {
        this.x = x;
        this.y = y;
        this.brancdid = brancdid;
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

    public int getBrancdid() {
        return brancdid;
    }

    public void setBrancdid(int brancdid) {
        this.brancdid = brancdid;
    }

}
