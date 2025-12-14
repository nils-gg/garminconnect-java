package com.garmin.connect.models;

import java.util.List;

/**
 * Hydration data for a day
 */
public class HydrationData {
    private String calendarDate;
    private long valueInML;
    private long goalInML;
    
    public String getCalendarDate() { return calendarDate; }
    public long getValueInML() { return valueInML; }
    public long getGoalInML() { return goalInML; }
}
