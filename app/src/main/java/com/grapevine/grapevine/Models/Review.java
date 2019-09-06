package com.grapevine.grapevine.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Review implements Parcelable {

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
    public Review(){
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(userid);
        parcel.writeInt(importance);
        parcel.writeInt(treeid);
        parcel.writeString(comment);
    }

    public Review(Parcel in){
        userid = in.readInt();
        importance = in.readInt();
        treeid = in.readInt();
        comment = in.readString();
    }
    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>()
    {
        public Review createFromParcel(Parcel in)
        {
            return new Review(in);
        }
        public Review[] newArray(int size)
        {
            return new Review[size];
        }
    };
}
