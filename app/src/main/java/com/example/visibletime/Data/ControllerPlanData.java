package com.example.visibletime.Data;

public class ControllerPlanData {
    String controllerName;            // 이름
    boolean isSelected;     // 사용자가 선택했는지 여부를 알기 위함

    public ControllerPlanData(String controllerName, boolean isSelected) {
        this.controllerName = controllerName;
        this.isSelected = isSelected;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
