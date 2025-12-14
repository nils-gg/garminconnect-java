package com.garmin.connect.models;

import java.util.List;

/**
 * User profile information
 */
public class UserProfile {
    private String displayName;
    private String fullName;
    private String emailAddress;
    private String profileImageUrlLarge;
    private String profileImageUrlMedium;
    private String profileImageUrlSmall;
    
    public String getDisplayName() { return displayName; }
    public String getFullName() { return fullName; }
    public String getEmailAddress() { return emailAddress; }
    public String getProfileImageUrlLarge() { return profileImageUrlLarge; }
    public String getProfileImageUrlMedium() { return profileImageUrlMedium; }
    public String getProfileImageUrlSmall() { return profileImageUrlSmall; }
}
