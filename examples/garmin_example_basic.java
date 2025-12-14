import com.garmin.connect.GarminConnect;
import com.garmin.connect.exceptions.GarminConnectException;
import com.garmin.connect.models.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Basic example demonstrating the use of the Garmin Connect Java library
 * 
 * This example shows:
 * - Authentication with Garmin Connect
 * - Retrieving user profile
 * - Getting daily statistics
 * - Accessing heart rate data
 * - Viewing recent activities
 */
public class BasicExample {
    
    public static void main(String[] args) {
        // Get credentials from environment variables or use defaults
        String email = System.getenv("GARMIN_EMAIL");
        String password = System.getenv("GARMIN_PASSWORD");
        
        if (email == null || password == null) {
            System.err.println("Please set GARMIN_EMAIL and GARMIN_PASSWORD environment variables");
            System.err.println("Example: export GARMIN_EMAIL=your@email.com");
            System.err.println("         export GARMIN_PASSWORD=yourpassword");
            System.exit(1);
        }
        
        try {
            System.out.println("=== Garmin Connect Java API - Basic Example ===\n");
            
            // 1. Initialize and login
            System.out.println("1. Logging in to Garmin Connect...");
            GarminConnect garmin = new GarminConnect(email, password);
            String displayName = garmin.login();
            System.out.println("   Successfully logged in as: " + displayName + "\n");
            
            // 2. Get user profile
            System.out.println("2. Retrieving user profile...");
            UserProfile profile = garmin.getUserProfile();
            System.out.println("   Full Name: " + profile.getFullName());
            System.out.println("   Email: " + profile.getEmailAddress() + "\n");
            
            // 3. Get user settings
            System.out.println("3. Retrieving user settings...");
            UserSettings settings = garmin.getUserSettings();
            System.out.println("   Unit System: " + settings.getUnitSystem());
            System.out.println("   Time Zone: " + settings.getTimeZone() + "\n");
            
            // 4. Get today's stats
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            System.out.println("4. Retrieving stats for " + today + "...");
            Stats stats = garmin.getStats(today);
            System.out.println("   Total Steps: " + stats.getTotalSteps());
            System.out.println("   Total Calories: " + stats.getTotalKilocalories() + " kcal");
            System.out.println("   Distance: " + String.format("%.2f", stats.getTotalDistanceMeters() / 1000) + " km");
            System.out.println("   Resting Heart Rate: " + stats.getRestingHeartRate() + " bpm\n");
            
            // 5. Get heart rate data
            System.out.println("5. Retrieving heart rate data...");
            HeartRateData hrData = garmin.getHeartRates(today);
            System.out.println("   Resting HR: " + hrData.getRestingHeartRate() + " bpm");
            System.out.println("   Max HR: " + hrData.getMaxHeartRate() + " bpm");
            System.out.println("   Min HR: " + hrData.getMinHeartRate() + " bpm\n");
            
            // 6. Get sleep data
            System.out.println("6. Retrieving sleep data...");
            SleepData sleepData = garmin.getSleepData(today);
            long sleepHours = sleepData.getSleepTimeSeconds() / 3600;
            long sleepMinutes = (sleepData.getSleepTimeSeconds() % 3600) / 60;
            System.out.println("   Total Sleep: " + sleepHours + "h " + sleepMinutes + "m");
            System.out.println("   Deep Sleep: " + (sleepData.getDeepSleepSeconds() / 60) + " min");
            System.out.println("   REM Sleep: " + (sleepData.getRemSleepSeconds() / 60) + " min\n");
            
            // 7. Get recent activities (last 7 days)
            String lastWeek = LocalDate.now().minusDays(7).format(DateTimeFormatter.ISO_DATE);
            System.out.println("7. Retrieving activities from " + lastWeek + " to " + today + "...");
            var activities = garmin.getActivitiesByDate(lastWeek, today, 10);
            System.out.println("   Found " + activities.size() + " activities:");
            
            for (Activity activity : activities) {
                System.out.println("   - " + activity.getActivityName() + 
                                 " (" + activity.getActivityType() + ")");
                System.out.println("     Date: " + activity.getStartTimeLocal());
                System.out.println("     Duration: " + (activity.getDuration() / 60) + " minutes");
                System.out.println("     Distance: " + String.format("%.2f", activity.getDistance() / 1000) + " km");
                System.out.println("     Calories: " + activity.getCalories() + " kcal");
                System.out.println();
            }
            
            // 8. Get stress data
            System.out.println("8. Retrieving stress data...");
            StressData stressData = garmin.getStressData(today);
            System.out.println("   Average Stress: " + stressData.getAverageStressLevel());
            System.out.println("   Max Stress: " + stressData.getMaxStressLevel());
            System.out.println("   Rest Time: " + (stressData.getRestStressSeconds() / 60) + " minutes\n");
            
            // 9. Get connected devices
            System.out.println("9. Retrieving connected devices...");
            var devices = garmin.getDevices();
            System.out.println("   Found " + devices.size() + " device(s):");
            for (Device device : devices) {
                System.out.println("   - " + device.getDisplayName() + 
                                 " (" + device.getProductDisplayName() + ")");
                System.out.println("     Status: " + (device.isActive() ? "Active" : "Inactive"));
                System.out.println("     Firmware: " + device.getFirmwareVersion());
            }
            System.out.println();
            
            // 10. Get personal records
            System.out.println("10. Retrieving personal records...");
            PersonalRecords records = garmin.getPersonalRecords();
            System.out.println("   Found " + records.getRecords().size() + " record(s):");
            for (PersonalRecords.PersonalRecord record : records.getRecords().subList(0, 
                    Math.min(5, records.getRecords().size()))) {
                System.out.println("   - " + record.getRecordType() + ": " + 
                                 record.getValue() + " " + record.getUnit() + 
                                 " (Date: " + record.getDate() + ")");
            }
            System.out.println();
            
            System.out.println("=== Example completed successfully! ===");
            
        } catch (GarminConnectException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
