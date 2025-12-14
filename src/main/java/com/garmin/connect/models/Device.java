package com.garmin.connect.models;

import java.util.List;
import java.util.Map;

/**
 * Connected device information
 */
public class Device {
    private long deviceId;
    private String displayName;
    private String partNumber;
    private String productDisplayName;
    private String softwareVersion;
    private String firmwareVersion;
    private long lastSyncTimeGMT;
    private String deviceType;
    private boolean active;
    
    public long getDeviceId() { return deviceId; }
    public String getDisplayName() { return displayName; }
    public String getPartNumber() { return partNumber; }
    public String getProductDisplayName() { return productDisplayName; }
    public String getSoftwareVersion() { return softwareVersion; }
    public String getFirmwareVersion() { return firmwareVersion; }
    public long getLastSyncTimeGMT() { return lastSyncTimeGMT; }
    public String getDeviceType() { return deviceType; }
    public boolean isActive() { return active; }
}
