package com.example.visibletime.Data;

public class ReportData implements Comparable<ReportData> {



    /*- 왜 int가 아니고 String인가?
        - 1. static 변수를 최대한 안쓰기 위함.
        - 2. 현재 static 0은 PARENT_TPYE으로 되어 있기 때문에 혹여나 혼돈을 줄 수 있기 때문
        - 값은 "so", "good", "bad"이 잇다.
        - "so"는 처음에 저장할 때 default로 저장되는 값이고 "good"이나 "bad"는 사용자가 리스트 선택지에서 고르게 되면 변경된다.
        - 다시 "good", "bad"에서 "so"로 갈때는 WriteDiaryActivity에서 삭제를 누를 때 이다.
    */
    String type;

    // 날짜 변수
    // 1. yyyyMMdd (데이터 검색용)
    // 2. hhmmss (데이터 정렬용)
    String dateForSearch;
    int dateForSort;

    String category;    // 공부
    String categoryParent;      // ex. 팀노바 활동.
    int level;          // 만족도    (스니펫으로 줄 예정)
    String content;     // 세부 내용
    int color;

    /*// ■ 시작,종료시간
    // (연산은 timeDuration 으로 하니, 시작시간과 종료시간은 int로 할 필요는 없을 듯 하다.)
    //      ● 시작 시간과 종료 시간 구하는 방법
    //          1. 종료 시간. (calendar Data로 현재시간을 구한다.)
    //          2. calendar.getTimeInMillis(); 로 현재 시간을 구한다.
    //          3. 구한 값에서 durationd으로 구한 숫자만큼 빼서 시작시간을 구한다.
    //          4. calendar.setTimeInMillis(millis);
    //          5. 새로 set한 calendar로 시작시간을 구한다.
    */

    String timeStart;      // 시작시간     03:00
    String timeEnd;        // 종료시간     04:00
    int timeDuration;   // 기간       s로 저장

    public ReportData() {
    }

    // Collections.sort(ArrayList)로 사용
    @Override
    public int compareTo(ReportData reportData) {
        if (this.dateForSort < reportData.getDateForSort()) {
            return 1;
        } else if (this.dateForSort > reportData.getDateForSort()) {
            return -1;
        }
        return 0;
    }


    public String getDateForSearch() {
        return dateForSearch;
    }

    public void setDateForSearch(String dateForSearch) {
        this.dateForSearch = dateForSearch;
    }

    public int getDateForSort() {
        return dateForSort;
    }

    public void setDateForSort(int dateForSort) {
        this.dateForSort = dateForSort;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(int timeDuration) {
        this.timeDuration = timeDuration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
