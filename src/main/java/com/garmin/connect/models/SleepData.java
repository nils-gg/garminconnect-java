package com.garmin.connect.models;

import java.util.List;

/**
 * Sleep data for a night
 */
public class SleepData {
    private String calendarDate;
    private long sleepTimeSeconds;
    private long napTimeSeconds;
    private long deepSleepSeconds;
    private long lightSleepSeconds;
    private long remSleepSeconds;
    private long awakeSleepSeconds;
    private int averageSpO2;
    private int lowestSpO2;
    private int sleepScore;
    
    public String getCalendarDate() { return calendarDate; }
    public long getSleepTimeSeconds() { return sleepTimeSeconds; }
    public long getNapTimeSeconds() { return napTimeSeconds; }
    public long getDeepSleepSeconds() { return deepSleepSeconds; }
    public long getLightSleepSeconds() { return lightSleepSeconds; }
    public long getRemSleepSeconds() { return remSleepSeconds; }
    public long getAwakeSleepSeconds() { return awakeSleepSeconds; }
    public int getAverageSpO2() { return averageSpO2; }
    public int getLowestSpO2() { return lowestSpO2; }
    public int getSleepScore() { return sleepScore; }
}
