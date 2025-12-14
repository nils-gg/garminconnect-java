# Garmin Connect Java API

A comprehensive Java API wrapper for Garmin Connect, providing programmatic access to health, fitness, and device data. This library is a pure Java port of the [python-garminconnect](https://github.com/cyberjunky/python-garminconnect) library.

## Features

- ✅ **OAuth Authentication** - Secure login with token persistence
- 📊 **Health Metrics** - Heart rate, sleep, stress, body composition, SpO2
- 🏃 **Activity Data** - Workouts, activities, exercise tracking
- ⌚ **Device Information** - Connected devices, settings, firmware
- 🏆 **Achievements** - Personal records, badges, challenges
- 📈 **Historical Data** - Trends, progress tracking, date range queries
- 💾 **Activity Downloads** - Export activities in TCX, GPX, or FIT format
- 🔄 **Automatic Token Refresh** - Seamless re-authentication

## Requirements

- Java 11 or higher
- Gson library for JSON processing

## Installation

### Maven

```xml
<dependencies>
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
</dependencies>
```

### Gradle

```gradle
dependencies {
    implementation 'com.google.code.gson:gson:2.10.1'
}
```

## Quick Start

```java
import com.garmin.connect.GarminConnect;
import com.garmin.connect.models.*;
import java.time.LocalDate;

public class QuickStart {
    public static void main(String[] args) throws Exception {
        // Initialize and login
        GarminConnect garmin = new GarminConnect(
            "your@email.com", 
            "yourpassword"
        );
        String displayName = garmin.login();
        System.out.println("Logged in as: " + displayName);
        
        // Get today's stats
        String today = LocalDate.now().toString();
        Stats stats = garmin.getStats(today);
        
        System.out.println("Steps: " + stats.getTotalSteps());
        System.out.println("Calories: " + stats.getTotalKilocalories());
        System.out.println("Distance: " + stats.getTotalDistanceMeters() / 1000 + " km");
        
        // Get heart rate data
        HeartRateData hr = garmin.getHeartRates(today);
        System.out.println("Resting HR: " + hr.getRestingHeartRate() + " bpm");
        
        // Get recent activities
        var activities = garmin.getActivitiesByDate(
            LocalDate.now().minusDays(7).toString(),
            today
        );
        
        for (Activity activity : activities) {
            System.out.println(activity.getActivityName() + 
                             " - " + activity.getDistance() / 1000 + " km");
        }
    }
}
```

## Authentication

The library uses OAuth authentication similar to the official Garmin Connect app. Tokens are automatically saved to `~/.garminconnect/` for persistent authentication.

### Token Storage

```java
// Use default token directory (~/.garminconnect)
GarminConnect garmin = new GarminConnect(email, password);

// Use custom token directory
GarminConnect garmin = new GarminConnect(email, password, "/path/to/tokens");
```

### Security

For production use, always:
- Store credentials securely (environment variables, key vault)
- Set restrictive permissions on token directory (700)
- Never commit credentials to version control

```bash
# Set secure permissions
chmod 700 ~/.garminconnect
chmod 600 ~/.garminconnect/*
```

## API Reference

### User & Profile

```java
// Get user profile
UserProfile profile = garmin.getUserProfile();

// Get user settings
UserSettings settings = garmin.getUserSettings();
```

### Daily Health & Activity

```java
String date = "2024-12-14"; // YYYY-MM-DD format

// Get daily stats
Stats stats = garmin.getStats(date);

// Get user summary
UserSummary summary = garmin.getUserSummary(date);

// Get heart rate data
HeartRateData hr = garmin.getHeartRates(date);

// Get sleep data
SleepData sleep = garmin.getSleepData(date);

// Get stress data
StressData stress = garmin.getStressData(date);

// Get steps data
StepsData steps = garmin.getStepsData(date);

// Get hydration data
HydrationData hydration = garmin.getHydrationData(date);
```

### Activities

```java
// Get activities by date range
List<Activity> activities = garmin.getActivitiesByDate(
    "2024-12-01", 
    "2024-12-14",
    20  // limit
);

// Get detailed activity information
ActivityDetails details = garmin.getActivityDetails(activityId);

// Download activity file
garmin.downloadActivity(
    activityId,
    GarminConnect.ActivityFormat.TCX,  // TCX, GPX, FIT, or ORIGINAL
    "output.tcx"
);
```

### Body Composition

```java
// Get body composition data
BodyComposition body = garmin.getBodyComposition(date);
System.out.println("Weight: " + body.getWeight() + " kg");
System.out.println("BMI: " + body.getBmi());
System.out.println("Body Fat: " + body.getBodyFat() + "%");
```

### Devices

```java
// Get connected devices
List<Device> devices = garmin.getDevices();

for (Device device : devices) {
    System.out.println(device.getDisplayName());
    System.out.println("Firmware: " + device.getFirmwareVersion());
}

// Get device settings
DeviceSettings settings = garmin.getDeviceSettings(deviceId);
```

### Achievements

```java
// Get personal records
PersonalRecords records = garmin.getPersonalRecords();

// Get badges
List<Badge> badges = garmin.getBadges();
```

## Examples

The library includes two comprehensive examples:

### BasicExample.java
Getting started guide showing:
- Authentication
- Retrieving user profile
- Getting daily statistics
- Accessing health metrics
- Viewing recent activities

Run with:
```bash
export GARMIN_EMAIL=your@email.com
export GARMIN_PASSWORD=yourpassword
java BasicExample
```

### AdvancedExample.java
Interactive demo with:
- Menu-driven interface
- Activity analysis
- Historical data reports
- Activity downloads
- Comprehensive health metrics
- Device management

Run with:
```bash
export GARMIN_EMAIL=your@email.com
export GARMIN_PASSWORD=yourpassword
java AdvancedExample
```

## Error Handling

The library provides specific exceptions for different error scenarios:

```java
try {
    GarminConnect garmin = new GarminConnect(email, password);
    garmin.login();
    
    Stats stats = garmin.getStats(date);
    
} catch (GarminConnectAuthenticationException e) {
    // Handle authentication errors (invalid credentials, etc.)
    System.err.println("Login failed: " + e.getMessage());
    
} catch (GarminConnectConnectionException e) {
    // Handle network/connection errors
    System.err.println("Connection error: " + e.getMessage());
    
} catch (GarminConnectTooManyRequestsException e) {
    // Handle rate limiting
    System.err.println("Rate limit exceeded");
    
} catch (GarminConnectException e) {
    // Handle other Garmin Connect errors
    System.err.println("Error: " + e.getMessage());
}
```

## Building from Source

### Prerequisites
- JDK 11 or higher
- Maven or Gradle

### Maven Build

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Package
mvn package
```

### Gradle Build

```bash
# Compile
./gradlew build

# Run tests
./gradlew test

# Create JAR
./gradlew jar
```

## Project Structure

```
src/main/java/com/garmin/connect/
├── GarminConnect.java           # Main API class
├── auth/
│   └── GarminAuthManager.java   # OAuth authentication
├── exceptions/
│   └── *.java                   # Exception classes
└── models/
    └── *.java                   # Data models

examples/
├── BasicExample.java            # Getting started example
└── AdvancedExample.java         # Comprehensive demo
```

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## Limitations

- This library requires valid Garmin Connect credentials
- Rate limiting applies - use responsibly
- Some features may require specific Garmin devices
- Historical data availability depends on your account

## Credits

This Java library is a port of the excellent [python-garminconnect](https://github.com/cyberjunky/python-garminconnect) library by cyberjunky.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Disclaimer

This library is not affiliated with, endorsed by, or connected to Garmin Ltd. or any of its subsidiaries. Use at your own risk.

## Support

For issues, questions, or contributions:
- Open an issue on GitHub
- Check existing issues for solutions
- Review the examples for usage patterns

## Version History

### 1.0.0 (Initial Release)
- Complete OAuth authentication
- All major API endpoints
- Comprehensive examples
- Full error handling
- Token persistence
