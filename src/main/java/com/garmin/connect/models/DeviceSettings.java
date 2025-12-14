package com.garmin.connect.models;

import java.util.List;
import java.util.Map;

/**
 * Device settings
 */
public class DeviceSettings {
    private long deviceId;
    private String timeZone;
    private String timeFormat;
    private String dateFormat;
    private String unitSystem;
    private boolean autoSyncEnabled;
    private int batteryLevel;
    
    public long getDeviceId() { return deviceId; }
    public String getTimeZone() { return timeZone; }
    public String getTimeFormat() { return timeFormat; }
    public String getDateFormat() { return dateFormat; }
    public String getUnitSystem() { return unitSystem; }
    public boolean isAutoSyncEnabled() { return autoSyncEnabled; }
    public int getBatteryLevel() { return batteryLevel; }
}
