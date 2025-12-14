# Getting Started with Garmin Connect Java API

This guide will help you set up and start using the Garmin Connect Java API library.

## Prerequisites

- **Java Development Kit (JDK) 11 or higher**
  - Check version: `java -version`
  - Download: [OpenJDK](https://openjdk.org/) or [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)

- **Build Tool** (choose one):
  - **Maven 3.6+**: [Download Maven](https://maven.apache.org/download.cgi)
  - **Gradle 7.0+**: [Download Gradle](https://gradle.org/install/)

- **Garmin Connect Account**
  - Sign up at [connect.garmin.com](https://connect.garmin.com)

## Quick Setup (5 Minutes)

### 1. Clone or Download the Project

```bash
# Using git
git clone https://github.com/yourusername/garmin-connect-java.git
cd garmin-connect-java

# Or download and extract ZIP from GitHub
```

### 2. Set Up Environment Variables

**Linux/macOS:**
```bash
export GARMIN_EMAIL="your@email.com"
export GARMIN_PASSWORD="yourpassword"
```

**Windows (Command Prompt):**
```cmd
set GARMIN_EMAIL=your@email.com
set GARMIN_PASSWORD=yourpassword
```

**Windows (PowerShell):**
```powershell
$env:GARMIN_EMAIL="your@email.com"
$env:GARMIN_PASSWORD="yourpassword"
```

**Permanent Setup (Linux/macOS):**
Add to your `~/.bashrc`, `~/.zshrc`, or `~/.bash_profile`:
```bash
export GARMIN_EMAIL="your@email.com"
export GARMIN_PASSWORD="yourpassword"
```

### 3. Build and Run

**Using Maven:**
```bash
# Build the project
mvn clean install

# Run basic example
mvn exec:java -Pbasic-example

# Run advanced example
mvn exec:java -Padvanced-example

# Create executable JAR
mvn package
java -jar target/garmin-connect-java-1.0.0-jar-with-dependencies.jar
```

**Using Gradle:**
```bash
# Build the project
./gradlew build

# Run basic example
./gradlew runBasicExample

# Run advanced example
./gradlew runAdvancedExample

# Create executable JAR
./gradlew fatJar
java -jar build/libs/garmin-connect-java-all-1.0.0.jar
```

## Project Structure

```
garmin-connect-java/
├── src/main/java/com/garmin/connect/
│   ├── GarminConnect.java           # Main API class
│   ├── auth/
│   │   └── GarminAuthManager.java   # Authentication manager
│   ├── exceptions/
│   │   └── *.java                   # Exception classes
│   └── models/
│       └── *.java                   # Data model classes
├── examples/
│   ├── BasicExample.java            # Getting started example
│   └── AdvancedExample.java         # Comprehensive demo
├── pom.xml                          # Maven configuration
├── build.gradle                     # Gradle configuration
└── README.md                        # Documentation
```

## First Program - Hello Garmin!

Create a file `HelloGarmin.java`:

```java
import com.garmin.connect.GarminConnect;
import com.garmin.connect.models.Stats;
import java.time.LocalDate;

public class HelloGarmin {
    public static void main(String[] args) {
        try {
            // Get credentials from environment
            String email = System.getenv("GARMIN_EMAIL");
            String password = System.getenv("GARMIN_PASSWORD");
            
            // Login
            System.out.println("Logging in...");
            GarminConnect garmin = new GarminConnect(email, password);
            String name = garmin.login();
            System.out.println("Hello, " + name + "!");
            
            // Get today's stats
            String today = LocalDate.now().toString();
            Stats stats = garmin.getStats(today);
            
            // Display summary
            System.out.println("\nToday's Activity:");
            System.out.println("Steps: " + stats.getTotalSteps());
            System.out.println("Calories: " + stats.getTotalKilocalories() + " kcal");
            System.out.println("Distance: " + String.format("%.2f km", 
                    stats.getTotalDistanceMeters() / 1000));
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
```

**Compile and run:**
```bash
# With Maven
mvn exec:java -Dexec.mainClass="HelloGarmin"

# With Gradle
./gradlew run -PmainClass=HelloGarmin

# Or directly with javac
javac -cp ".:lib/*" HelloGarmin.java
java -cp ".:lib/*" HelloGarmin
```

## Common Use Cases

### 1. Daily Health Summary

```java
import com.garmin.connect.GarminConnect;
import com.garmin.connect.models.*;

public class DailyHealthSummary {
    public static void main(String[] args) throws Exception {
        GarminConnect garmin = new GarminConnect(
            System.getenv("GARMIN_EMAIL"),
            System.getenv("GARMIN_PASSWORD")
        );
        garmin.login();
        
        String today = java.time.LocalDate.now().toString();
        
        // Get various health metrics
        Stats stats = garmin.getStats(today);
        HeartRateData hr = garmin.getHeartRates(today);
        SleepData sleep = garmin.getSleepData(today);
        StressData stress = garmin.getStressData(today);
        
        // Display comprehensive summary
        System.out.println("=== Daily Health Summary ===");
        System.out.println("\nActivity:");
        System.out.println("  Steps: " + stats.getTotalSteps());
        System.out.println("  Calories: " + stats.getTotalKilocalories());
        
        System.out.println("\nHeart Rate:");
        System.out.println("  Resting: " + hr.getRestingHeartRate() + " bpm");
        System.out.println("  Max: " + hr.getMaxHeartRate() + " bpm");
        
        System.out.println("\nSleep:");
        System.out.println("  Total: " + sleep.getSleepTimeSeconds() / 3600 + " hours");
        System.out.println("  Deep: " + sleep.getDeepSleepSeconds() / 60 + " min");
        
        System.out.println("\nStress:");
        System.out.println("  Average: " + stress.getAverageStressLevel());
    }
}
```

### 2. Activity Tracker

```java
import com.garmin.connect.GarminConnect;
import com.garmin.connect.models.Activity;
import java.time.LocalDate;
import java.util.List;

public class ActivityTracker {
    public static void main(String[] args) throws Exception {
        GarminConnect garmin = new GarminConnect(
            System.getenv("GARMIN_EMAIL"),
            System.getenv("GARMIN_PASSWORD")
        );
        garmin.login();
        
        // Get last 7 days of activities
        String today = LocalDate.now().toString();
        String lastWeek = LocalDate.now().minusDays(7).toString();
        
        List<Activity> activities = garmin.getActivitiesByDate(
            lastWeek, today, 20
        );
        
        System.out.println("Recent Activities (" + activities.size() + "):");
        System.out.println("==========================================");
        
        double totalDistance = 0;
        int totalCalories = 0;
        
        for (Activity activity : activities) {
            System.out.printf("\n%s (%s)%n", 
                activity.getActivityName(),
                activity.getActivityType()
            );
            System.out.printf("  Date: %s%n", activity.getStartTimeLocal());
            System.out.printf("  Duration: %d min%n", 
                activity.getDuration() / 60);
            System.out.printf("  Distance: %.2f km%n", 
                activity.getDistance() / 1000);
            System.out.printf("  Calories: %d kcal%n", 
                activity.getCalories());
            
            totalDistance += activity.getDistance();
            totalCalories += activity.getCalories();
        }
        
        System.out.println("\n==========================================");
        System.out.printf("Total Distance: %.2f km%n", totalDistance / 1000);
        System.out.printf("Total Calories: %,d kcal%n", totalCalories);
    }
}
```

### 3. Export Activities

```java
import com.garmin.connect.GarminConnect;
import com.garmin.connect.models.Activity;
import java.util.List;

public class ActivityExporter {
    public static void main(String[] args) throws Exception {
        GarminConnect garmin = new GarminConnect(
            System.getenv("GARMIN_EMAIL"),
            System.getenv("GARMIN_PASSWORD")
        );
        garmin.login();
        
        // Get recent activities
        String today = java.time.LocalDate.now().toString();
        String lastMonth = java.time.LocalDate.now().minusDays(30).toString();
        
        List<Activity> activities = garmin.getActivitiesByDate(
            lastMonth, today, 10
        );
        
        System.out.println("Exporting activities...");
        
        for (Activity activity : activities) {
            String filename = String.format("activity_%d_%s.tcx",
                activity.getActivityId(),
                activity.getActivityType()
            );
            
            garmin.downloadActivity(
                activity.getActivityId(),
                GarminConnect.ActivityFormat.TCX,
                filename
            );
            
            System.out.println("Exported: " + filename);
        }
        
        System.out.println("Done! Exported " + activities.size() + " activities.");
    }
}
```

## Troubleshooting

### Issue: "Login failed" or Authentication Error

**Solution:**
1. Verify your credentials are correct
2. Check if 2FA is enabled (not fully supported yet)
3. Try logging in via the website first
4. Check token directory permissions: `chmod 700 ~/.garminconnect`

### Issue: "Connection timeout" or Network Error

**Solution:**
1. Check your internet connection
2. Verify Garmin Connect website is accessible
3. Check if you're behind a proxy/firewall
4. Try again later (Garmin servers might be down)

### Issue: "Rate limit exceeded"

**Solution:**
1. Reduce the frequency of API calls
2. Add delays between requests: `Thread.sleep(1000)`
3. Cache data locally instead of fetching repeatedly
4. Garmin limits apply - respect their usage policies

### Issue: Missing dependencies

**Maven:**
```bash
mvn dependency:resolve
mvn clean install -U
```

**Gradle:**
```bash
./gradlew clean build --refresh-dependencies
```

### Issue: "No data available"

**Solution:**
1. Ensure your Garmin device is synced
2. Check if data exists for the requested date
3. Verify your account has the required permissions
4. Some metrics require specific devices

## Best Practices

### 1. Secure Credential Storage

**Never hardcode credentials!** Use:

- **Environment variables** (shown above)
- **Property files** (add to `.gitignore`)
- **System keyring/keychain**
- **Cloud secret managers** (AWS Secrets Manager, Azure Key Vault)

### 2. Error Handling

Always wrap API calls in try-catch:

```java
try {
    Stats stats = garmin.getStats(date);
    // Process stats
} catch (GarminConnectAuthenticationException e) {
    // Handle auth errors
    System.err.println("Authentication failed: " + e.getMessage());
} catch (GarminConnectConnectionException e) {
    // Handle connection errors
    System.err.println("Connection error: " + e.getMessage());
} catch (GarminConnectException e) {
    // Handle other errors
    System.err.println("API error: " + e.getMessage());
}
```

### 3. Rate Limiting

Be respectful of Garmin's API:

```java
// Add delays between requests
Thread.sleep(1000); // 1 second delay

// Batch requests when possible
List<Activity> activities = garmin.getActivitiesByDate(start, end, 50);
// Process all at once instead of individual calls
```

### 4. Token Management

Tokens are automatically managed and stored in `~/.garminconnect/`:

```bash
# Set secure permissions
chmod 700 ~/.garminconnect
chmod 600 ~/.garminconnect/*

# Tokens are automatically refreshed when expired
# No manual intervention needed
```

## Next Steps

1. **Explore the Examples**
   - Run `BasicExample.java` for getting started
   - Run `AdvancedExample.java` for comprehensive features

2. **Read the API Documentation**
   - Check `README.md` for complete API reference
   - Review model classes for available data fields

3. **Build Your Application**
   - Start with a simple use case
   - Add error handling
   - Implement your business logic

4. **Contribute**
   - Report bugs on GitHub
   - Submit feature requests
   - Contribute improvements

## Additional Resources

- **Official Garmin Connect**: https://connect.garmin.com
- **Original Python Library**: https://github.com/cyberjunky/python-garminconnect
- **Java Documentation**: https://docs.oracle.com/en/java/
- **Maven Guide**: https://maven.apache.org/guides/
- **Gradle Guide**: https://docs.gradle.org/

## Support

Need help?
- Check the examples directory for usage patterns
- Review error messages carefully
- Search existing GitHub issues
- Open a new issue with details about your problem

---

**Happy coding! 🏃‍♂️💻**
