package com.garmin.connect.models;

import java.util.List;

/**
 * Stress data for a day
 */
public class StressData {
    private String calendarDate;
    private int overallStressLevel;
    private int maxStressLevel;
    private int averageStressLevel;
    private long restStressSeconds;
    private long activityStressSeconds;
    private long uncategorizedStressSeconds;
    private long lowStressSeconds;
    private long mediumStressSeconds;
    private long highStressSeconds;
    
    public String getCalendarDate() { return calendarDate; }
    public int getOverallStressLevel() { return overallStressLevel; }
    public int getMaxStressLevel() { return maxStressLevel; }
    public int getAverageStressLevel() { return averageStressLevel; }
    public long getRestStressSeconds() { return restStressSeconds; }
    public long getActivityStressSeconds() { return activityStressSeconds; }
    public long getUncategorizedStressSeconds() { return uncategorizedStressSeconds; }
    public long getLowStressSeconds() { return lowStressSeconds; }
    public long getMediumStressSeconds() { return mediumStressSeconds; }
    public long getHighStressSeconds() { return highStressSeconds; }
}
