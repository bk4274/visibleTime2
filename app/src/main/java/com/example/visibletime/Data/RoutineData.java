package com.example.visibletime.Data;

public class RoutineData {
    String name;
    String dateForSearch; //1. yyyyMMdd (데이터 검색용)
    boolean isRunning;

    public RoutineData() {
    }

    public RoutineData(String name, String dateForSearch, boolean isRunning) {
        this.name = name;
        this.dateForSearch = dateForSearch;
        this.isRunning = isRunning;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateForSearch() {
        return dateForSearch;
    }

    public void setDateForSearch(String dateForSearch) {
        this.dateForSearch = dateForSearch;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
