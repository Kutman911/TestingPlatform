
package com.project.model;


public class Test {
    private int id;
    private String title;
    private int duration; // in minutes
    private boolean published;

    public Test() { }

    public Test(int id, String title, int duration, boolean published) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.published = published;
    }

    public Test(String title, int duration, boolean published) {
        this.title = title;
        this.duration = duration;
        this.published = published;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    @Override
    public String toString() {
        return title;
    }
}
