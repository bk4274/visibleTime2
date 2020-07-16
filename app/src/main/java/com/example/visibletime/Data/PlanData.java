package com.example.visibletime.Data;

public class PlanData {

    String routine;
    String dateForSearch; //1. yyyyMMdd (데이터 검색용)
    String category;
    String categoryParent;
    int color;
    int type;           // Parent 인지, Child 인지 구분하기 위함
    int targetTime;     // 목표 시간, 실행 시간
    boolean isOn;

    public PlanData() {

    }

    public PlanData(String routine, String dateForSearch, String category, String categoryParent, int color, int type, int targetTime) {
        this.routine = routine;
        this.dateForSearch = dateForSearch;
        this.category = category;
        this.categoryParent = categoryParent;
        this.color = color;
        this.type = type;
        this.targetTime = targetTime;
    }

    public PlanData(String category, String categoryParent, int color, int type, int targetTime, boolean isOn) {
        this.category = category;
        this.categoryParent = categoryParent;
        this.color = color;
        this.type = type;
        this.isOn = isOn;
    }

    public String getRoutine() {
        return routine;
    }

    public void setRoutine(String routine) {
        this.routine = routine;
    }

    public String getDateForSearch() {
        return dateForSearch;
    }

    public void setDateForSearch(String dateForSearch) {
        this.dateForSearch = dateForSearch;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryParent() {
        return categoryParent;
    }

    public void setCategoryParent(String categoryParent) {
        this.categoryParent = categoryParent;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(int targetTime) {
        this.targetTime = targetTime;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }
}
