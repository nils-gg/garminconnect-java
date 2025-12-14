package com.garmin.connect.models;

import java.util.List;

/**
 * User settings
 */
public class UserSettings {
    private String unitSystem;
    private String distanceUnit;
    private String elevationUnit;
    private String weightUnit;
    private String temperatureUnit;
    private String locale;
    private String timeZone;
    
    public String getUnitSystem() { return unitSystem; }
    public String getDistanceUnit() { return distanceUnit; }
    public String getElevationUnit() { return elevationUnit; }
    public String getWeightUnit() { return weightUnit; }
    public String getTemperatureUnit() { return temperatureUnit; }
    public String getLocale() { return locale; }
    public String getTimeZone() { return timeZone; }
}
