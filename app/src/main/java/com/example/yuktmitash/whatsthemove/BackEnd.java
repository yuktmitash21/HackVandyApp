package com.example.yuktmitash.whatsthemove;

import java.util.ArrayList;

public class BackEnd {
    private ArrayList<Party> myParties = new ArrayList<>();
    private User user;
    private String userid;
    //on data change listener rather than single event


    public BackEnd(User user, String userid) {
        this.user = user;
        this.userid = userid;
    }

    public ArrayList<Party> getMyParties() {
        return myParties;
    }

    public void setMyParties(Party party) {
        myParties.add(party);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserid() {
        return userid;
    }
}
