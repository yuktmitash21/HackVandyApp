package com.example.yuktmitash.whatsthemove;

public class CustomDate {
    private int month;
    private int day;
    private int year;
    private int hours;
    private int minutes;

    public CustomDate(int month, int day, int year, int hours, int minutes) {
        this.month = month;
        this.day = day;
        this.year = year;
        this.hours = hours;
        this.minutes = minutes;
    }

    public int getDay() {
        return day;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

}
