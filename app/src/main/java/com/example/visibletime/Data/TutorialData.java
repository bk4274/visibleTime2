package com.example.visibletime.Data;

public class TutorialData {
    int image;
    String name;
    String context;

    public TutorialData(int image, String name, String context) {
        this.image = image;
        this.name = name;
        this.context = context;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
