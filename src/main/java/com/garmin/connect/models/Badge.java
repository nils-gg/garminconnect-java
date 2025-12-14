package com.garmin.connect.models;

import java.util.List;
import java.util.Map;

/**
 * Achievement badge
 */
public class Badge {
    private long badgeId;
    private String badgeKey;
    private String badgeName;
    private String badgeDescription;
    private String badgeCategory;
    private String badgeImageUrl;
    private String earnedDate;
    private boolean earned;
    private int progress;
    private int target;
    
    public long getBadgeId() { return badgeId; }
    public String getBadgeKey() { return badgeKey; }
    public String getBadgeName() { return badgeName; }
    public String getBadgeDescription() { return badgeDescription; }
    public String getBadgeCategory() { return badgeCategory; }
    public String getBadgeImageUrl() { return badgeImageUrl; }
    public String getEarnedDate() { return earnedDate; }
    public boolean isEarned() { return earned; }
    public int getProgress() { return progress; }
    public int getTarget() { return target; }
}
