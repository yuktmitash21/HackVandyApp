package com.example.yuktmitash.whatsthemove;

import java.util.ArrayList;
import java.util.Calendar;

public class User {
    private String userName;
    private int age;
    private String password;
    private String email;
    private boolean atParty = false;
    private int monthBorn;
    private int dayBorn;
    private ArrayList<Party> Parties;

    private double lattitude;
    private double longitude;
    private int numParties;
    private String gender;

    public User(String userName, int age, String password, String email, int monthBorn, int dayBorn,
    String gender) {
        this.userName = userName;
        this.age = age;
        this.password = password;
        this.email = email;
        this.monthBorn = monthBorn;
        this.dayBorn = dayBorn;
        this.gender = gender;


    }

    public User() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isAtParty() {
        return atParty;
    }

    public void setAtParty(boolean atParty) {
        this.atParty = atParty;
    }

    public void reCalcAge() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if(month == monthBorn && day == dayBorn) {
            age++;
        }

    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void addParty(Party party) {
        Parties.add(party);
    }

    public ArrayList<Party> getParties() {
        return Parties;
    }

    public String getGender() {
        return gender;
    }

    public void setGender() {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object obj) {
        User user = (User) obj;
        return obj instanceof User && email.equals(user.getEmail());
    }
}

