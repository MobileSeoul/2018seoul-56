package com.teamnoname.streetartzone.Data;

import io.realm.RealmObject;

public class GroupReviewData extends RealmObject {

    int seq;
    int score;
    String writer;
    String date;
    String contents;


    public GroupReviewData() {
    }


    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }


    public int getscore() {
        return score;
    }

    public void setscore(int score) {
        this.score = score;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
