package com.garmin.connect.models;

import java.util.List;

/**
 * Daily activity statistics
 */
public class Stats {
    private long totalKilocalories;
    private long activeKilocalories;
    private long bmrKilocalories;
    private long totalSteps;
    private double totalDistanceMeters;
    private long activeTimeSeconds;
    private long sedentaryTimeSeconds;
    private long sleepTimeSeconds;
    private int averageStressLevel;
    private int maxStressLevel;
    private int restingHeartRate;
    private int maxHeartRate;
    private int averageHeartRate;
    
    public long getTotalKilocalories() { return totalKilocalories; }
    public long getActiveKilocalories() { return activeKilocalories; }
    public long getBmrKilocalories() { return bmrKilocalories; }
    public long getTotalSteps() { return totalSteps; }
    public double getTotalDistanceMeters() { return totalDistanceMeters; }
    public long getActiveTimeSeconds() { return activeTimeSeconds; }
    public long getSedentaryTimeSeconds() { return sedentaryTimeSeconds; }
    public long getSleepTimeSeconds() { return sleepTimeSeconds; }
    public int getAverageStressLevel() { return averageStressLevel; }
    public int getMaxStressLevel() { return maxStressLevel; }
    public int getRestingHeartRate() { return restingHeartRate; }
    public int getMaxHeartRate() { return maxHeartRate; }
    public int getAverageHeartRate() { return averageHeartRate; }
}
