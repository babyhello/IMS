package com.example.yujhaochen.ims;

public class Image {

    private String small;
    private String medium;
    private String large;
    private String name;
    private String timestamp;

    public Image() {
        super();
    }

    public Image(String small, String medium, String large, String name, String timestamp) {
        this.small = small;
        this.medium = medium;
        this.large = large;
        this.name = name;
        this.timestamp = timestamp;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}