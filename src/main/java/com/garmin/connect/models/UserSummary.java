package com.garmin.connect.models;

import java.util.List;

/**
 * User summary for a day
 */
public class UserSummary {
    private String calendarDate;
    private long totalSteps;
    private double totalDistanceMeters;
    private long activeTimeSeconds;
    private long totalKilocalories;
    private int averageStressLevel;
    private int restingHeartRate;
    
    public String getCalendarDate() { return calendarDate; }
    public long getTotalSteps() { return totalSteps; }
    public double getTotalDistanceMeters() { return totalDistanceMeters; }
    public long getActiveTimeSeconds() { return activeTimeSeconds; }
    public long getTotalKilocalories() { return totalKilocalories; }
    public int getAverageStressLevel() { return averageStressLevel; }
    public int getRestingHeartRate() { return restingHeartRate; }
}
