package com.garmin.connect.models;

import java.util.List;
import java.util.Map;

/**
 * Personal records
 */
public class PersonalRecords {
    private List<PersonalRecord> records;
    
    public List<PersonalRecord> getRecords() { return records; }
    
    public static class PersonalRecord {
        private String recordType;
        private double value;
        private String unit;
        private String date;
        private long activityId;
        private String activityName;
        
        public String getRecordType() { return recordType; }
        public double getValue() { return value; }
        public String getUnit() { return unit; }
        public String getDate() { return date; }
        public long getActivityId() { return activityId; }
        public String getActivityName() { return activityName; }
    }
}
