package com.garmin.connect.auth;

import com.garmin.connect.exceptions.GarminConnectAuthenticationException;
import com.garmin.connect.exceptions.GarminConnectConnectionException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages authentication with Garmin Connect using OAuth
 * Handles token storage, refresh, and session management
 */
public class GarminAuthManager {
    
    private static final String SSO_URL = "https://sso.garmin.com/sso";
    private static final String SIGNIN_URL = SSO_URL + "/signin";
    private static final String TOKEN_URL = "https://connect.garmin.com/modern/di-oauth/exchange";
    
    private final String email;
    private final String password;
    private final String tokenDirectory;
    private final HttpClient httpClient;
    private final Gson gson;
    
    private String accessToken;
    private String refreshToken;
    private long tokenExpiry;
    
    public GarminAuthManager(String email, String password, String tokenDirectory, 
            HttpClient httpClient) {
        this.email = email;
        this.password = password;
        this.tokenDirectory = tokenDirectory;
        this.httpClient = httpClient;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    /**
     * Login to Garmin Connect
     * Attempts to load existing tokens first, falls back to fresh login if needed
     */
    public void login() throws GarminConnectAuthenticationException, 
            GarminConnectConnectionException {
        // Try to load existing tokens
        if (loadTokens()) {
            try {
                // Verify tokens are still valid
                if (System.currentTimeMillis() < tokenExpiry) {
                    return; // Tokens are valid
                } else {
                    // Tokens expired, try to refresh
                    refreshTokens();
                    return;
                }
            } catch (Exception e) {
                // Token refresh failed, proceed with fresh login
            }
        }
        
        // Perform fresh login
        performLogin();
    }
    
    /**
     * Perform OAuth login flow
     */
    private void performLogin() throws GarminConnectAuthenticationException, 
            GarminConnectConnectionException {
        try {
            // Step 1: Get the login page to extract CSRF token
            HttpRequest loginPageRequest = HttpRequest.newBuilder()
                    .uri(URI.create(SIGNIN_URL + "?service=https://connect.garmin.com/modern"))
                    .GET()
                    .build();
            
            HttpResponse<String> loginPageResponse = httpClient.send(loginPageRequest,
                    HttpResponse.BodyHandlers.ofString());
            
            String csrfToken = extractCsrfToken(loginPageResponse.body());
            if (csrfToken == null) {
                throw new GarminConnectAuthenticationException("Failed to extract CSRF token");
            }
            
            // Step 2: Submit login credentials
            Map<String, String> loginData = new HashMap<>();
            loginData.put("username", email);
            loginData.put("password", password);
            loginData.put("embed", "false");
            loginData.put("_csrf", csrfToken);
            
            String formBody = buildFormBody(loginData);
            
            HttpRequest loginRequest = HttpRequest.newBuilder()
                    .uri(URI.create(SIGNIN_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formBody))
                    .build();
            
            HttpResponse<String> loginResponse = httpClient.send(loginRequest,
                    HttpResponse.BodyHandlers.ofString());
            
            // Check for authentication errors
            if (loginResponse.body().contains("Invalid") || 
                    loginResponse.body().contains("error")) {
                throw new GarminConnectAuthenticationException(
                        "Invalid credentials or login failed");
            }
            
            // Step 3: Extract ticket and exchange for OAuth tokens
            String ticket = extractTicket(loginResponse);
            if (ticket == null) {
                throw new GarminConnectAuthenticationException("Failed to extract ticket");
            }
            
            exchangeTicketForTokens(ticket);
            
            // Save tokens for future use
            saveTokens();
            
        } catch (IOException | InterruptedException e) {
            throw new GarminConnectConnectionException("Connection error during login", e);
        }
    }
    
    /**
     * Exchange ticket for OAuth tokens
     */
    private void exchangeTicketForTokens(String ticket) throws IOException, InterruptedException,
            GarminConnectAuthenticationException {
        HttpRequest tokenRequest = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL + "?ticket=" + ticket))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        
        HttpResponse<String> tokenResponse = httpClient.send(tokenRequest,
                HttpResponse.BodyHandlers.ofString());
        
        if (tokenResponse.statusCode() != 200) {
            throw new GarminConnectAuthenticationException(
                    "Failed to exchange ticket for tokens: " + tokenResponse.statusCode());
        }
        
        // Parse token response
        Map<String, Object> tokenData = gson.fromJson(tokenResponse.body(), Map.class);
        
        this.accessToken = (String) tokenData.get("access_token");
        this.refreshToken = (String) tokenData.get("refresh_token");
        
        // Calculate token expiry (typically 1 hour)
        Number expiresIn = (Number) tokenData.get("expires_in");
        if (expiresIn != null) {
            this.tokenExpiry = System.currentTimeMillis() + (expiresIn.longValue() * 1000);
        } else {
            // Default to 1 hour if not specified
            this.tokenExpiry = System.currentTimeMillis() + (3600 * 1000);
        }
    }
    
    /**
     * Refresh OAuth tokens using refresh token
     */
    public void refreshTokens() throws GarminConnectAuthenticationException, 
            GarminConnectConnectionException {
        if (refreshToken == null) {
            throw new GarminConnectAuthenticationException("No refresh token available");
        }
        
        try {
            Map<String, String> refreshData = new HashMap<>();
            refreshData.put("refresh_token", refreshToken);
            refreshData.put("grant_type", "refresh_token");
            
            String formBody = buildFormBody(refreshData);
            
            HttpRequest refreshRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://connect.garmin.com/modern/di-oauth/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(refreshRequest,
                    HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                // Refresh failed, need to login again
                performLogin();
                return;
            }
            
            Map<String, Object> tokenData = gson.fromJson(response.body(), Map.class);
            this.accessToken = (String) tokenData.get("access_token");
            
            Number expiresIn = (Number) tokenData.get("expires_in");
            if (expiresIn != null) {
                this.tokenExpiry = System.currentTimeMillis() + (expiresIn.longValue() * 1000);
            }
            
            saveTokens();
            
        } catch (IOException | InterruptedException e) {
            throw new GarminConnectConnectionException("Error refreshing tokens", e);
        }
    }
    
    /**
     * Ensure we have valid authentication tokens
     */
    public void ensureAuthenticated() throws GarminConnectAuthenticationException, 
            GarminConnectConnectionException {
        if (accessToken == null || System.currentTimeMillis() >= tokenExpiry) {
            if (refreshToken != null) {
                refreshTokens();
            } else {
                performLogin();
            }
        }
    }
    
    /**
     * Get current access token
     */
    public String getAccessToken() {
        return accessToken;
    }
    
    /**
     * Save tokens to disk for persistent authentication
     */
    private void saveTokens() {
        try {
            Path tokenDir = Paths.get(tokenDirectory);
            Files.createDirectories(tokenDir);
            
            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("access_token", accessToken);
            tokenData.put("refresh_token", refreshToken);
            tokenData.put("expiry", tokenExpiry);
            
            String json = gson.toJson(tokenData);
            Files.writeString(tokenDir.resolve("tokens.json"), json);
            
        } catch (IOException e) {
            // Non-critical error, just log it
            System.err.println("Warning: Failed to save tokens: " + e.getMessage());
        }
    }
    
    /**
     * Load tokens from disk
     */
    private boolean loadTokens() {
        try {
            Path tokenFile = Paths.get(tokenDirectory, "tokens.json");
            if (!Files.exists(tokenFile)) {
                return false;
            }
            
            String json = Files.readString(tokenFile);
            Map<String, Object> tokenData = gson.fromJson(json, Map.class);
            
            this.accessToken = (String) tokenData.get("access_token");
            this.refreshToken = (String) tokenData.get("refresh_token");
            this.tokenExpiry = ((Number) tokenData.get("expiry")).longValue();
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Logout and clear tokens
     */
    public void logout() {
        try {
            Path tokenFile = Paths.get(tokenDirectory, "tokens.json");
            Files.deleteIfExists(tokenFile);
        } catch (IOException e) {
            System.err.println("Warning: Failed to delete tokens: " + e.getMessage());
        }
        
        this.accessToken = null;
        this.refreshToken = null;
        this.tokenExpiry = 0;
    }
    
    /**
     * Extract CSRF token from HTML
     */
    private String extractCsrfToken(String html) {
        Pattern pattern = Pattern.compile("name=\"_csrf\"\\s+value=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    /**
     * Extract ticket from response
     */
    private String extractTicket(HttpResponse<String> response) {
        // Check headers for ticket
        String location = response.headers().firstValue("Location").orElse("");
        Pattern pattern = Pattern.compile("ticket=([^&]+)");
        Matcher matcher = pattern.matcher(location);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // Check body for ticket
        matcher = pattern.matcher(response.body());
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    /**
     * Build URL-encoded form body from map
     */
    private String buildFormBody(Map<String, String> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                   .append("=")
                   .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return builder.toString();
    }
}
