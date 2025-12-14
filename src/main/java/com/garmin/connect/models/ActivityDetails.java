package com.garmin.connect.models;

import java.util.List;
import java.util.Map;

/**
 * Detailed activity information
 */
public class ActivityDetails {
    private long activityId;
    private String activityName;
    private String activityType;
    private String startTimeLocal;
    private long duration;
    private double distance;
    private int calories;
    private int averageHR;
    private int maxHR;
    private double elevationGain;
    private double elevationLoss;
    private double minElevation;
    private double maxElevation;
    private double averagePace;
    private double maxPace;
    private List<ActivitySplit> splits;
    private Map<String, Object> metadata;
    
    public long getActivityId() { return activityId; }
    public String getActivityName() { return activityName; }
    public String getActivityType() { return activityType; }
    public String getStartTimeLocal() { return startTimeLocal; }
    public long getDuration() { return duration; }
    public double getDistance() { return distance; }
    public int getCalories() { return calories; }
    public int getAverageHR() { return averageHR; }
    public int getMaxHR() { return maxHR; }
    public double getElevationGain() { return elevationGain; }
    public double getElevationLoss() { return elevationLoss; }
    public double getMinElevation() { return minElevation; }
    public double getMaxElevation() { return maxElevation; }
    public double getAveragePace() { return averagePace; }
    public double getMaxPace() { return maxPace; }
    public List<ActivitySplit> getSplits() { return splits; }
    public Map<String, Object> getMetadata() { return metadata; }
    
    public static class ActivitySplit {
        private int splitIndex;
        private double distance;
        private long duration;
        private double avgSpeed;
        private int avgHR;
        
        public int getSplitIndex() { return splitIndex; }
        public double getDistance() { return distance; }
        public long getDuration() { return duration; }
        public double getAvgSpeed() { return avgSpeed; }
        public int getAvgHR() { return avgHR; }
    }
}
