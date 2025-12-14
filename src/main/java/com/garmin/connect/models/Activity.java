package com.garmin.connect.models;

import java.util.List;
import java.util.Map;

/**
 * Activity summary
 */
public class Activity {
    private long activityId;
    private String activityName;
    private String activityType;
    private String startTimeLocal;
    private String startTimeGMT;
    private long duration;
    private double distance;
    private double averageSpeed;
    private double maxSpeed;
    private int calories;
    private int averageHR;
    private int maxHR;
    private String description;
    
    public long getActivityId() { return activityId; }
    public String getActivityName() { return activityName; }
    public String getActivityType() { return activityType; }
    public String getStartTimeLocal() { return startTimeLocal; }
    public String getStartTimeGMT() { return startTimeGMT; }
    public long getDuration() { return duration; }
    public double getDistance() { return distance; }
    public double getAverageSpeed() { return averageSpeed; }
    public double getMaxSpeed() { return maxSpeed; }
    public int getCalories() { return calories; }
    public int getAverageHR() { return averageHR; }
    public int getMaxHR() { return maxHR; }
    public String getDescription() { return description; }
}
