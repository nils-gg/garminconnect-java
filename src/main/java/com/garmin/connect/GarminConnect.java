package com.garmin.connect;

import com.garmin.connect.auth.GarminAuthManager;
import com.garmin.connect.exceptions.*;
import com.garmin.connect.models.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java API wrapper for Garmin Connect
 * Provides access to health, fitness, and device data from Garmin Connect
 */
public class GarminConnect {
    
    private static final String BASE_URL = "https://connect.garmin.com";
    private static final String MODERN_API = BASE_URL + "/modern";
    private static final String PROXY_API = BASE_URL + "/proxy";
    
    private final HttpClient httpClient;
    private final Gson gson;
    private final GarminAuthManager authManager;
    private final String tokenDirectory;
    
    /**
     * Creates a new GarminConnect instance
     * 
     * @param email Garmin Connect email
     * @param password Garmin Connect password
     */
    public GarminConnect(String email, String password) {
        this(email, password, System.getProperty("user.home") + "/.garminconnect");
    }
    
    /**
     * Creates a new GarminConnect instance with custom token directory
     * 
     * @param email Garmin Connect email
     * @param password Garmin Connect password
     * @param tokenDirectory Directory to store authentication tokens
     */
    public GarminConnect(String email, String password, String tokenDirectory) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.tokenDirectory = tokenDirectory;
        this.authManager = new GarminAuthManager(email, password, tokenDirectory, httpClient);
    }
    
    /**
     * Login to Garmin Connect
     * 
     * @return Display name of the logged-in user
     * @throws GarminConnectAuthenticationException if authentication fails
     * @throws GarminConnectConnectionException if connection fails
     */
    public String login() throws GarminConnectAuthenticationException, GarminConnectConnectionException {
        authManager.login();
        
        try {
            UserProfile profile = getUserProfile();
            return profile.getDisplayName();
        } catch (Exception e) {
            throw new GarminConnectAuthenticationException("Failed to retrieve user profile after login", e);
        }
    }
    
    /**
     * Get user profile information
     * 
     * @return UserProfile object
     * @throws GarminConnectException if request fails
     */
    public UserProfile getUserProfile() throws GarminConnectException {
        String url = PROXY_API + "/userprofile-service/userprofile";
        String response = makeApiRequest(url);
        return gson.fromJson(response, UserProfile.class);
    }
    
    /**
     * Get user settings
     * 
     * @return UserSettings object
     * @throws GarminConnectException if request fails
     */
    public UserSettings getUserSettings() throws GarminConnectException {
        String url = PROXY_API + "/userprofile-service/userprofile/settings";
        String response = makeApiRequest(url);
        return gson.fromJson(response, UserSettings.class);
    }
    
    /**
     * Get activity summary stats for a specific date
     * 
     * @param date Date in ISO format (YYYY-MM-DD)
     * @return Stats object containing daily statistics
     * @throws GarminConnectException if request fails
     */
    public Stats getStats(String date) throws GarminConnectException {
        String url = PROXY_API + "/usersummary-service/stats/daily/" + date;
        String response = makeApiRequest(url);
        return gson.fromJson(response, Stats.class);
    }
    
    /**
     * Get user summary for a specific date
     * 
     * @param date Date in ISO format (YYYY-MM-DD)
     * @return UserSummary object
     * @throws GarminConnectException if request fails
     */
    public UserSummary getUserSummary(String date) throws GarminConnectException {
        String url = PROXY_API + "/usersummary-service/usersummary/daily/" + date;
        String response = makeApiRequest(url);
        return gson.fromJson(response, UserSummary.class);
    }
    
    /**
     * Get heart rate data for a specific date
     * 
     * @param date Date in ISO format (YYYY-MM-DD)
     * @return HeartRateData object
     * @throws GarminConnectException if request fails
     */
    public HeartRateData getHeartRates(String date) throws GarminConnectException {
        String url = PROXY_API + "/wellness-service/wellness/dailyHeartRate/" + date;
        String response = makeApiRequest(url);
        return gson.fromJson(response, HeartRateData.class);
    }
    
    /**
     * Get sleep data for a specific date
     * 
     * @param date Date in ISO format (YYYY-MM-DD)
     * @return SleepData object
     * @throws GarminConnectException if request fails
     */
    public SleepData getSleepData(String date) throws GarminConnectException {
        String url = PROXY_API + "/wellness-service/wellness/dailySleepData/" + date;
        String response = makeApiRequest(url);
        return gson.fromJson(response, SleepData.class);
    }
    
    /**
     * Get stress data for a specific date
     * 
     * @param date Date in ISO format (YYYY-MM-DD)
     * @return StressData object
     * @throws GarminConnectException if request fails
     */
    public StressData getStressData(String date) throws GarminConnectException {
        String url = PROXY_API + "/wellness-service/wellness/dailyStress/" + date;
        String response = makeApiRequest(url);
        return gson.fromJson(response, StressData.class);
    }
    
    /**
     * Get body composition data for a specific date
     * 
     * @param date Date in ISO format (YYYY-MM-DD)
     * @return BodyComposition object
     * @throws GarminConnectException if request fails
     */
    public BodyComposition getBodyComposition(String date) throws GarminConnectException {
        String url = PROXY_API + "/weight-service/weight/dateRange?startDate=" + 
                     date + "&endDate=" + date;
        String response = makeApiRequest(url);
        return gson.fromJson(response, BodyComposition.class);
    }
    
    /**
     * Get list of activities within a date range
     * 
     * @param startDate Start date in ISO format (YYYY-MM-DD)
     * @param endDate End date in ISO format (YYYY-MM-DD)
     * @param limit Maximum number of activities to return
     * @return List of Activity objects
     * @throws GarminConnectException if request fails
     */
    public List<Activity> getActivitiesByDate(String startDate, String endDate, int limit) 
            throws GarminConnectException {
        String url = PROXY_API + "/activitylist-service/activities/search/activities?" +
                     "startDate=" + startDate + "&endDate=" + endDate + "&limit=" + limit;
        String response = makeApiRequest(url);
        
        TypeToken<List<Activity>> typeToken = new TypeToken<List<Activity>>() {};
        return gson.fromJson(response, typeToken.getType());
    }
    
    /**
     * Get list of activities within a date range (default limit of 20)
     * 
     * @param startDate Start date in ISO format (YYYY-MM-DD)
     * @param endDate End date in ISO format (YYYY-MM-DD)
     * @return List of Activity objects
     * @throws GarminConnectException if request fails
     */
    public List<Activity> getActivitiesByDate(String startDate, String endDate) 
            throws GarminConnectException {
        return getActivitiesByDate(startDate, endDate, 20);
    }
    
    /**
     * Get detailed activity information
     * 
     * @param activityId Activity ID
     * @return ActivityDetails object
     * @throws GarminConnectException if request fails
     */
    public ActivityDetails getActivityDetails(long activityId) throws GarminConnectException {
        String url = PROXY_API + "/activity-service/activity/" + activityId;
        String response = makeApiRequest(url);
        return gson.fromJson(response, ActivityDetails.class);
    }
    
    /**
     * Get list of connected devices
     * 
     * @return List of Device objects
     * @throws GarminConnectException if request fails
     */
    public List<Device> getDevices() throws GarminConnectException {
        String url = PROXY_API + "/device-service/deviceregistration/devices";
        String response = makeApiRequest(url);
        
        TypeToken<List<Device>> typeToken = new TypeToken<List<Device>>() {};
        return gson.fromJson(response, typeToken.getType());
    }
    
    /**
     * Get device settings for a specific device
     * 
     * @param deviceId Device ID
     * @return DeviceSettings object
     * @throws GarminConnectException if request fails
     */
    public DeviceSettings getDeviceSettings(long deviceId) throws GarminConnectException {
        String url = PROXY_API + "/device-service/deviceservice/device-info/settings/" + deviceId;
        String response = makeApiRequest(url);
        return gson.fromJson(response, DeviceSettings.class);
    }
    
    /**
     * Get steps data for a specific date
     * 
     * @param date Date in ISO format (YYYY-MM-DD)
     * @return StepsData object
     * @throws GarminConnectException if request fails
     */
    public StepsData getStepsData(String date) throws GarminConnectException {
        String url = PROXY_API + "/wellness-service/wellness/dailySteps/" + date;
        String response = makeApiRequest(url);
        return gson.fromJson(response, StepsData.class);
    }
    
    /**
     * Get hydration data for a specific date
     * 
     * @param date Date in ISO format (YYYY-MM-DD)
     * @return HydrationData object
     * @throws GarminConnectException if request fails
     */
    public HydrationData getHydrationData(String date) throws GarminConnectException {
        String url = PROXY_API + "/usersummary-service/usersummary/hydration/daily/" + date;
        String response = makeApiRequest(url);
        return gson.fromJson(response, HydrationData.class);
    }
    
    /**
     * Get personal records
     * 
     * @return PersonalRecords object
     * @throws GarminConnectException if request fails
     */
    public PersonalRecords getPersonalRecords() throws GarminConnectException {
        String url = PROXY_API + "/personalrecord-service/personalrecord/prs";
        String response = makeApiRequest(url);
        return gson.fromJson(response, PersonalRecords.class);
    }
    
    /**
     * Get available badges
     * 
     * @return List of Badge objects
     * @throws GarminConnectException if request fails
     */
    public List<Badge> getBadges() throws GarminConnectException {
        String url = PROXY_API + "/badge-service/badge/available";
        String response = makeApiRequest(url);
        
        TypeToken<List<Badge>> typeToken = new TypeToken<List<Badge>>() {};
        return gson.fromJson(response, typeToken.getType());
    }
    
    /**
     * Download activity file in specified format
     * 
     * @param activityId Activity ID
     * @param format Format (TCX, GPX, FIT, ORIGINAL)
     * @param outputPath Path to save the file
     * @throws GarminConnectException if request fails
     */
    public void downloadActivity(long activityId, ActivityFormat format, String outputPath) 
            throws GarminConnectException {
        String url = switch (format) {
            case TCX -> MODERN_API + "/proxy/download-service/export/tcx/activity/" + activityId;
            case GPX -> MODERN_API + "/proxy/download-service/export/gpx/activity/" + activityId;
            case FIT -> MODERN_API + "/proxy/download-service/files/activity/" + activityId;
            case ORIGINAL -> MODERN_API + "/proxy/download-service/files/activity/" + activityId;
        };
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + authManager.getAccessToken())
                    .GET()
                    .build();
            
            HttpResponse<byte[]> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofByteArray());
            
            if (response.statusCode() != 200) {
                throw new GarminConnectException("Failed to download activity: " + 
                        response.statusCode());
            }
            
            Files.write(Paths.get(outputPath), response.body());
        } catch (IOException | InterruptedException e) {
            throw new GarminConnectConnectionException("Error downloading activity", e);
        }
    }
    
    /**
     * Logout and clear tokens
     */
    public void logout() {
        authManager.logout();
    }
    
    /**
     * Make an authenticated API request
     * 
     * @param url API endpoint URL
     * @return Response body as string
     * @throws GarminConnectException if request fails
     */
    private String makeApiRequest(String url) throws GarminConnectException {
        try {
            // Ensure we have valid tokens
            authManager.ensureAuthenticated();
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + authManager.getAccessToken())
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 401) {
                // Token expired, refresh and retry
                authManager.refreshTokens();
                return makeApiRequest(url);
            } else if (response.statusCode() == 429) {
                throw new GarminConnectTooManyRequestsException("Rate limit exceeded");
            } else if (response.statusCode() >= 400) {
                throw new GarminConnectException("API request failed: " + response.statusCode() + 
                        " - " + response.body());
            }
            
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new GarminConnectConnectionException("Connection error", e);
        }
    }
    
    /**
     * Activity download formats
     */
    public enum ActivityFormat {
        TCX, GPX, FIT, ORIGINAL
    }
}
