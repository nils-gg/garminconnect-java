package com.garmin.connect.models;

import java.util.List;

/**
 * Heart rate data for a day
 */
public class HeartRateData {
    private String calendarDate;
    private int restingHeartRate;
    private int maxHeartRate;
    private int minHeartRate;
    private List<HeartRateValue> heartRateValues;
    
    public String getCalendarDate() { return calendarDate; }
    public int getRestingHeartRate() { return restingHeartRate; }
    public int getMaxHeartRate() { return maxHeartRate; }
    public int getMinHeartRate() { return minHeartRate; }
    public List<HeartRateValue> getHeartRateValues() { return heartRateValues; }
    
    public static class HeartRateValue {
        private long timestamp;
        private int heartRate;
        
        public long getTimestamp() { return timestamp; }
        public int getHeartRate() { return heartRate; }
    }
}
