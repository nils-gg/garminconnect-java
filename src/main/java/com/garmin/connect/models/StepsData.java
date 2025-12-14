package com.garmin.connect.models;

import java.util.List;

/**
 * Steps data for a day
 */
public class StepsData {
    private String calendarDate;
    private long totalSteps;
    private long goalSteps;
    private List<StepValue> stepValues;
    
    public String getCalendarDate() { return calendarDate; }
    public long getTotalSteps() { return totalSteps; }
    public long getGoalSteps() { return goalSteps; }
    public List<StepValue> getStepValues() { return stepValues; }
    
    public static class StepValue {
        private long startGMT;
        private long endGMT;
        private int steps;
        
        public long getStartGMT() { return startGMT; }
        public long getEndGMT() { return endGMT; }
        public int getSteps() { return steps; }
    }
}
