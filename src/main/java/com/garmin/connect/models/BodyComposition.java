package com.garmin.connect.models;

import java.util.List;

/**
 * Body composition data
 */
public class BodyComposition {
    private double weight;
    private double bmi;
    private double bodyFat;
    private double bodyWater;
    private double boneMass;
    private double muscleMass;
    private String sourceType;
    private long timestampGMT;
    
    public double getWeight() { return weight; }
    public double getBmi() { return bmi; }
    public double getBodyFat() { return bodyFat; }
    public double getBodyWater() { return bodyWater; }
    public double getBoneMass() { return boneMass; }
    public double getMuscleMass() { return muscleMass; }
    public String getSourceType() { return sourceType; }
    public long getTimestampGMT() { return timestampGMT; }
}
