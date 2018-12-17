package com.teamnoname.streetartzone.Data;

import io.realm.RealmObject;

public class TicketData extends RealmObject {

    String ticketPath;
    String coverPath;

    public String getTicketPath() {
        return ticketPath;
    }

    public void setTicketPath(String ticketPath) {
        this.ticketPath = ticketPath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }
}
