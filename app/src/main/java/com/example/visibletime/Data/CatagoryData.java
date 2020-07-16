package com.example.visibletime.Data;

import java.util.ArrayList;
import java.util.List;

public class CatagoryData {

    public static final int PARENT_TYPE = 0;
    public static final int CHILD_TYPE = 1;

    private int type;           // Parent 인지, Child 인지 구분하기 위함
    private String name;
    private int color;       // Chile 시 색상
    private String parentName;  // Child 일때 부모의 이름을 가지고 있다.
    private int count;          // Parent 시 Child가 몇개인지 부여주기 위함
    public ArrayList<CatagoryData> invisibleChildren;

    public CatagoryData() {
    }

    public CatagoryData(int type, String name, int color, String parentName, int count) {
        this.type = type;
        this.name = name;
        this.color = color;
        this.parentName = parentName;
        this.count = count;
    }

    public static int getParentType() {
        return PARENT_TYPE;
    }

    public static int getChildType() {
        return CHILD_TYPE;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
