package com.teamnoname.streetartzone.Data;

public class GroupReviewDataItem {

    int score;
    String writer;
    String date;
    String contents;
    int seq;

    public GroupReviewDataItem() {
    }

    public GroupReviewDataItem(int score, String writer, String date, String contents, int seq) {
        this.score = score;
        this.writer = writer;
        this.date = date;
        this.contents = contents;
        this.seq = seq;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
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

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
