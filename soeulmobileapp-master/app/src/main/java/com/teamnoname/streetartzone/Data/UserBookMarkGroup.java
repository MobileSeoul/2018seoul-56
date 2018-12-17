package com.teamnoname.streetartzone.Data;

import java.util.Date;

import io.realm.RealmModel;
import io.realm.RealmObject;

public class UserBookMarkGroup extends RealmObject {

    int groupSeq;
    Date addDate;

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public int getGroupSeq() {
        return groupSeq;
    }

    public void setGroupSeq(int groupSeq) {
        this.groupSeq = groupSeq;
    }
}
