package com.example.visibletime.Data;

public class DiaryData {
    // 1. yyyyMMdd (데이터 검색용)
    String dateForSearch;
    // 텍스트 데이터, 이미지 데이터
    String goodText, badText, willText, photo;

    public DiaryData() {
    }

    public DiaryData(String dateForSearch, String goodText, String badText, String willText, String photo) {
        this.dateForSearch = dateForSearch;
        this.goodText = goodText;
        this.badText = badText;
        this.willText = willText;
        this.photo = photo;
    }

    public String getDateForSearch() {
        return dateForSearch;
    }

    public void setDateForSearch(String dateForSearch) {
        this.dateForSearch = dateForSearch;
    }

    public String getGoodText() {
        return goodText;
    }

    public void setGoodText(String goodText) {
        this.goodText = goodText;
    }

    public String getBadText() {
        return badText;
    }

    public void setBadText(String badText) {
        this.badText = badText;
    }

    public String getWillText() {
        return willText;
    }

    public void setWillText(String willText) {
        this.willText = willText;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
