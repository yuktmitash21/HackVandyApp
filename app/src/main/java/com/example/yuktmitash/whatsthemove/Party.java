package com.example.yuktmitash.whatsthemove;

import android.provider.Telephony;

import java.time.LocalDateTime;
import java.util.Date;

public class Party implements Comparable {
    private long averageAge;
    private long promotions;
    private double longitude;
    private double lattitude;
    private long rating;
    private String name;
    private String sponsor;
    private String address;

    private boolean isMove;
    private String litness;

    private String fireid;

    private float distance;

    private String sortBy;
    private float people;

    private int maleCount;
    private int femaleCount;





    public Party(long promotions, double longitude, double lattitude, long rating, boolean isMove, String litness, String name,
    String sponsor, String address, int maleCount, int femaleCount) {
        this.promotions = promotions;
        this.longitude = longitude;
        this.lattitude = lattitude;
        this.rating = rating;
        this.isMove = isMove;
        this.litness = litness;
        this.name = name;
        this.sponsor = sponsor;
        this.address = address;
        this.maleCount = maleCount;
        this.femaleCount = femaleCount;
    }

    public Party() {}

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public void setMove(boolean move) {
        isMove = move;
    }

    public void setLitness(String litness) {
        this.litness = litness;
    }

    public void setPromotions(long promotions) {
        this.promotions = promotions;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public String getLitness() {
        return litness;
    }

    public boolean isMove() {
        return isMove;
    }

    public double getLattitude() {
        return lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getPromotions() {
        return promotions;
    }

    public long getRating() {
        return rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSponsor() {
        return sponsor;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public void setAverageAge(long averageAge) {
        this.averageAge = averageAge;
    }

    public long getAverageAge() {
        return averageAge;
    }

    public void setFireid(String fireid) {
        this.fireid = fireid;
    }

    public String getFireid() {
        return fireid;
    }

    @Override
    public String toString() {
        return name;
    }

    public float getDistance() {
        return distance;
    }
    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int compareTo(Object that) {
        Party other = (Party) that;

        if (sortBy.equals("distance")) {
            return (int) ((this.getDistance() * 100) - (other.getDistance() * 100));
        } else if (sortBy.equals("promotions")) {
            return (int) (other.getPromotions() - this.getPromotions());
        } else if (sortBy.equals("people")) {
           return (int)  (other.getPeople() - this.getPeople());
        } else {
            return (int) (other.getRating() - this.getRating());
        }
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public float getPeople() {
        return people;
    }

    public void setPeople(float people) {
        this.people = people;
    }

    public int getFemaleCount() {
        return femaleCount;
    }

    public int getMaleCount() {
        return maleCount;
    }

    public void setFemaleCount(int femaleCount) {
        this.femaleCount = femaleCount;
    }

    public void setMaleCount(int maleCount) {
        this.maleCount = maleCount;
    }
}
