package com.example.visibletime.Data;

public class AlarmData {

    boolean isChecked;
    String name, am_pm, weekName;
    int hour_24, hour_12, minute;
    boolean [] arrayWeek;

    public AlarmData() {
    }

    public AlarmData(boolean isChecked, String name, String am_pm, String weekName, int hour_24, int hour_12, int minute, boolean[] arrayWeek) {
        this.isChecked = isChecked;
        this.name = name;
        this.am_pm = am_pm;
        this.weekName = weekName;
        this.hour_24 = hour_24;
        this.hour_12 = hour_12;
        this.minute = minute;
        this.arrayWeek = arrayWeek;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAm_pm() {
        return am_pm;
    }

    public void setAm_pm(String am_pm) {
        this.am_pm = am_pm;
    }

    public String getWeekName() {
        return weekName;
    }

    public void setWeekName(String weekName) {
        this.weekName = weekName;
    }

    public int getHour_24() {
        return hour_24;
    }

    public void setHour_24(int hour_24) {
        this.hour_24 = hour_24;
    }

    public int getHour_12() {
        return hour_12;
    }

    public void setHour_12(int hour_12) {
        this.hour_12 = hour_12;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean[] getArrayWeek() {
        return arrayWeek;
    }

    public void setArrayWeek(boolean[] arrayWeek) {
        this.arrayWeek = arrayWeek;
    }
}
