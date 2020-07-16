package com.example.visibletime.Data;

public class StatisticsData implements Comparable<StatisticsData> {
    String category;
    String categoryParent;
    int color;
    int type;           // Parent 인지, Child 인지 구분하기 위함
    int targetTime, actionTime;     // 목표 시간, 실행 시간

    // 생성자
    public StatisticsData( ) {
    }

    public StatisticsData(String category, String categoryParent, int color, int type, int targetTime, int actionTime) {
        this.category = category;
        this.categoryParent = categoryParent;
        this.color = color;
        this.type = type;
        this.targetTime = targetTime;
        this.actionTime = actionTime;
    }

    // ArrayList를 행동 시간이 많은 카테고리가 맨 위에 배치되어 내림차순으로 정렬하기 위한 메서드
    // Collections.sort(ArrayList)로 사용
    @Override
    public int compareTo(StatisticsData statisticsData) {
        if (this.actionTime < statisticsData.getActionTime()) {
            return 1;
        } else if (this.actionTime > statisticsData.getActionTime()) {
            return -1;
        }
        return 0;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public int getActionTime() {
        return actionTime;
    }

    public void setActionTime(int actionTime) {
        this.actionTime = actionTime;
    }

    public String getCategoryParent() {
        return categoryParent;
    }

    public void setCategoryParent(String categoryParent) {
        this.categoryParent = categoryParent;
    }
}
