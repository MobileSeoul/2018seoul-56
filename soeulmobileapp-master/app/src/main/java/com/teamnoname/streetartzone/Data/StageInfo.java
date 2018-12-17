package com.teamnoname.streetartzone.Data;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class StageInfo extends RealmObject implements Serializable {

    @PrimaryKey
    private int seq;
    private String district;
    private String placeName;
    private String address;

    public StageInfo() {
    }

    public int getSeq() {
        return seq;
    }

    public String getDistrict() {
        return district;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getAddress() {
        return address;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
