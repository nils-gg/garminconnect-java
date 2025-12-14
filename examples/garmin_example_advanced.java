import com.garmin.connect.GarminConnect;
import com.garmin.connect.exceptions.GarminConnectException;
import com.garmin.connect.models.*;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Advanced example demonstrating comprehensive use of the Garmin Connect Java library
 * 
 * This example includes:
 * - Interactive menu system
 * - Activity analysis and statistics
 * - Activity file downloads
 * - Historical data queries
 * - Error handling
 */
public class AdvancedExample {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static GarminConnect garmin;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    
    public static void main(String[] args) {
        String email = System.getenv("GARMIN_EMAIL");
        String password = System.getenv("GARMIN_PASSWORD");
        
        if (email == null || password == null) {
            System.err.println("Please set GARMIN_EMAIL and GARMIN_PASSWORD environment variables");
            System.exit(1);
        }
        
        try {
            // Login
            System.out.println("🏃‍♂️ Garmin Connect Java API - Advanced Demo");
            System.out.println("=============================================\n");
            System.out.print("Logging in... ");
            
            garmin = new GarminConnect(email, password);
            String displayName = garmin.login();
            
            System.out.println("✓ Logged in as: " + displayName + "\n");
            
            // Show main menu
            showMainMenu();
            
        } catch (GarminConnectException e) {
            System.err.println("\n❌ Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void showMainMenu() {
        while (true) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. View Today's Summary");
            System.out.println("2. Activity Analysis");
            System.out.println("3. Historical Data Report");
            System.out.println("4. Download Activities");
            System.out.println("5. Health Metrics");
            System.out.println("6. Device Information");
            System.out.println("7. Achievements & Records");
            System.out.println("0. Exit");
            System.out.print("\nSelect option: ");
            
            String choice = scanner.nextLine().trim();
            
            try {
                switch (choice) {
                    case "1" -> viewTodaysSummary();
                    case "2" -> activityAnalysis();
                    case "3" -> historicalDataReport();
                    case "4" -> downloadActivities();
                    case "5" -> healthMetrics();
                    case "6" -> deviceInformation();
                    case "7" -> achievementsAndRecords();
                    case "0" -> {
                        System.out.println("\nGoodbye! 👋");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (GarminConnectException e) {
                System.err.println("❌ Error: " + e.getMessage());
            }
        }
    }
    
    private static void viewTodaysSummary() throws GarminConnectException {
        String today = LocalDate.now().format(dateFormatter);
        
        System.out.println("\n📊 Today's Summary (" + today + ")");
        System.out.println("================================\n");
        
        // Get comprehensive stats
        Stats stats = garmin.getStats(today);
        UserSummary summary = garmin.getUserSummary(today);
        
        System.out.println("🚶 Activity:");
        System.out.println("  Steps: " + String.format("%,d", stats.getTotalSteps()));
        System.out.println("  Distance: " + String.format("%.2f km", 
                stats.getTotalDistanceMeters() / 1000));
        System.out.println("  Active Time: " + formatSeconds(stats.getActiveTimeSeconds()));
        System.out.println("  Sedentary Time: " + formatSeconds(stats.getSedentaryTimeSeconds()));
        
        System.out.println("\n🔥 Calories:");
        System.out.println("  Total: " + stats.getTotalKilocalories() + " kcal");
        System.out.println("  Active: " + stats.getActiveKilocalories() + " kcal");
        System.out.println("  BMR: " + stats.getBmrKilocalories() + " kcal");
        
        System.out.println("\n❤️ Heart Rate:");
        System.out.println("  Resting: " + stats.getRestingHeartRate() + " bpm");
        System.out.println("  Average: " + stats.getAverageHeartRate() + " bpm");
        System.out.println("  Maximum: " + stats.getMaxHeartRate() + " bpm");
        
        System.out.println("\n😰 Stress:");
        System.out.println("  Average Level: " + stats.getAverageStressLevel());
        System.out.println("  Maximum Level: " + stats.getMaxStressLevel());
    }
    
    private static void activityAnalysis() throws GarminConnectException {
        System.out.print("\nEnter number of days to analyze (e.g., 7 for last week): ");
        int days = Integer.parseInt(scanner.nextLine().trim());
        
        String endDate = LocalDate.now().format(dateFormatter);
        String startDate = LocalDate.now().minusDays(days).format(dateFormatter);
        
        System.out.println("\n🏃 Activity Analysis");
        System.out.println("Period: " + startDate + " to " + endDate);
        System.out.println("====================================\n");
        
        List<Activity> activities = garmin.getActivitiesByDate(startDate, endDate, 50);
        
        if (activities.isEmpty()) {
            System.out.println("No activities found in this period.");
            return;
        }
        
        // Calculate statistics
        int totalActivities = activities.size();
        long totalDuration = 0;
        double totalDistance = 0;
        int totalCalories = 0;
        
        for (Activity activity : activities) {
            totalDuration += activity.getDuration();
            totalDistance += activity.getDistance();
            totalCalories += activity.getCalories();
        }
        
        System.out.println("Summary:");
        System.out.println("  Total Activities: " + totalActivities);
        System.out.println("  Total Duration: " + formatSeconds(totalDuration));
        System.out.println("  Total Distance: " + String.format("%.2f km", totalDistance / 1000));
        System.out.println("  Total Calories: " + String.format("%,d kcal", totalCalories));
        System.out.println("  Average per Activity:");
        System.out.println("    - Duration: " + formatSeconds(totalDuration / totalActivities));
        System.out.println("    - Distance: " + String.format("%.2f km", 
                (totalDistance / totalActivities) / 1000));
        System.out.println("    - Calories: " + (totalCalories / totalActivities) + " kcal");
        
        System.out.println("\nRecent Activities:");
        for (int i = 0; i < Math.min(10, activities.size()); i++) {
            Activity activity = activities.get(i);
            System.out.println("\n  " + (i + 1) + ". " + activity.getActivityName() + 
                             " (" + activity.getActivityType() + ")");
            System.out.println("     Date: " + activity.getStartTimeLocal());
            System.out.println("     Duration: " + formatSeconds(activity.getDuration()));
            System.out.println("     Distance: " + String.format("%.2f km", 
                    activity.getDistance() / 1000));
            System.out.println("     Avg HR: " + activity.getAverageHR() + " bpm");
            System.out.println("     Calories: " + activity.getCalories() + " kcal");
        }
        
        // Offer to view detailed info
        System.out.print("\nView details for activity number (0 to skip): ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        
        if (choice > 0 && choice <= activities.size()) {
            Activity activity = activities.get(choice - 1);
            viewActivityDetails(activity.getActivityId());
        }
    }
    
    private static void viewActivityDetails(long activityId) throws GarminConnectException {
        System.out.println("\n📋 Activity Details");
        System.out.println("==================\n");
        
        ActivityDetails details = garmin.getActivityDetails(activityId);
        
        System.out.println("Name: " + details.getActivityName());
        System.out.println("Type: " + details.getActivityType());
        System.out.println("Date: " + details.getStartTimeLocal());
        System.out.println("Duration: " + formatSeconds(details.getDuration()));
        System.out.println("Distance: " + String.format("%.2f km", details.getDistance() / 1000));
        
        System.out.println("\nHeart Rate:");
        System.out.println("  Average: " + details.getAverageHR() + " bpm");
        System.out.println("  Maximum: " + details.getMaxHR() + " bpm");
        
        System.out.println("\nElevation:");
        System.out.println("  Gain: " + String.format("%.0f m", details.getElevationGain()));
        System.out.println("  Loss: " + String.format("%.0f m", details.getElevationLoss()));
        System.out.println("  Min: " + String.format("%.0f m", details.getMinElevation()));
        System.out.println("  Max: " + String.format("%.0f m", details.getMaxElevation()));
        
        if (details.getSplits() != null && !details.getSplits().isEmpty()) {
            System.out.println("\nSplits:");
            for (ActivityDetails.ActivitySplit split : details.getSplits()) {
                System.out.println("  " + split.getSplitIndex() + ": " + 
                        formatSeconds(split.getDuration()) + " - " +
                        String.format("%.2f km/h", split.getAvgSpeed() * 3.6));
            }
        }
    }
    
    private static void historicalDataReport() throws GarminConnectException {
        System.out.print("\nEnter number of days for report (e.g., 30): ");
        int days = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.println("\n📈 Historical Data Report");
        System.out.println("Last " + days + " days");
        System.out.println("========================\n");
        
        long totalSteps = 0;
        long totalCalories = 0;
        double totalDistance = 0;
        int daysWithData = 0;
        
        LocalDate currentDate = LocalDate.now();
        
        System.out.println("Collecting data...");
        
        for (int i = 0; i < days; i++) {
            LocalDate date = currentDate.minusDays(i);
            String dateStr = date.format(dateFormatter);
            
            try {
                Stats stats = garmin.getStats(dateStr);
                totalSteps += stats.getTotalSteps();
                totalCalories += stats.getTotalKilocalories();
                totalDistance += stats.getTotalDistanceMeters();
                daysWithData++;
            } catch (Exception e) {
                // Skip days with no data
            }
        }
        
        if (daysWithData == 0) {
            System.out.println("No data available for this period.");
            return;
        }
        
        System.out.println("\nTotals:");
        System.out.println("  Steps: " + String.format("%,d", totalSteps));
        System.out.println("  Calories: " + String.format("%,d kcal", totalCalories));
        System.out.println("  Distance: " + String.format("%.2f km", totalDistance / 1000));
        
        System.out.println("\nDaily Averages:");
        System.out.println("  Steps: " + String.format("%,d", totalSteps / daysWithData));
        System.out.println("  Calories: " + String.format("%,d kcal", totalCalories / daysWithData));
        System.out.println("  Distance: " + String.format("%.2f km", 
                (totalDistance / daysWithData) / 1000));
    }
    
    private static void downloadActivities() throws GarminConnectException {
        System.out.print("\nEnter number of recent activities to list: ");
        int limit = Integer.parseInt(scanner.nextLine().trim());
        
        String today = LocalDate.now().format(dateFormatter);
        String lastMonth = LocalDate.now().minusDays(30).format(dateFormatter);
        
        List<Activity> activities = garmin.getActivitiesByDate(lastMonth, today, limit);
        
        if (activities.isEmpty()) {
            System.out.println("No activities found.");
            return;
        }
        
        System.out.println("\nRecent Activities:");
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            System.out.println((i + 1) + ". " + activity.getActivityName() + 
                             " (" + activity.getStartTimeLocal() + ")");
        }
        
        System.out.print("\nSelect activity to download (0 to cancel): ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        
        if (choice <= 0 || choice > activities.size()) {
            return;
        }
        
        Activity activity = activities.get(choice - 1);
        
        System.out.println("\nSelect format:");
        System.out.println("1. TCX");
        System.out.println("2. GPX");
        System.out.println("3. FIT");
        System.out.print("Choice: ");
        
        String formatChoice = scanner.nextLine().trim();
        GarminConnect.ActivityFormat format = switch (formatChoice) {
            case "1" -> GarminConnect.ActivityFormat.TCX;
            case "2" -> GarminConnect.ActivityFormat.GPX;
            case "3" -> GarminConnect.ActivityFormat.FIT;
            default -> {
                System.out.println("Invalid format.");
                yield null;
            }
        };
        
        if (format == null) return;
        
        String filename = "activity_" + activity.getActivityId() + 
                         "." + format.name().toLowerCase();
        
        System.out.print("\nDownloading... ");
        garmin.downloadActivity(activity.getActivityId(), format, filename);
        System.out.println("✓ Saved to " + filename);
    }
    
    private static void healthMetrics() throws GarminConnectException {
        String today = LocalDate.now().format(dateFormatter);
        
        System.out.println("\n💪 Health Metrics (" + today + ")");
        System.out.println("===================================\n");
        
        // Sleep data
        SleepData sleep = garmin.getSleepData(today);
        System.out.println("😴 Sleep:");
        System.out.println("  Total: " + formatSeconds(sleep.getSleepTimeSeconds()));
        System.out.println("  Deep: " + formatSeconds(sleep.getDeepSleepSeconds()));
        System.out.println("  Light: " + formatSeconds(sleep.getLightSleepSeconds()));
        System.out.println("  REM: " + formatSeconds(sleep.getRemSleepSeconds()));
        System.out.println("  Awake: " + formatSeconds(sleep.getAwakeSleepSeconds()));
        if (sleep.getSleepScore() > 0) {
            System.out.println("  Score: " + sleep.getSleepScore());
        }
        
        // Stress data
        StressData stress = garmin.getStressData(today);
        System.out.println("\n😰 Stress:");
        System.out.println("  Average: " + stress.getAverageStressLevel());
        System.out.println("  Maximum: " + stress.getMaxStressLevel());
        System.out.println("  Rest: " + formatSeconds(stress.getRestStressSeconds()));
        System.out.println("  Low: " + formatSeconds(stress.getLowStressSeconds()));
        System.out.println("  Medium: " + formatSeconds(stress.getMediumStressSeconds()));
        System.out.println("  High: " + formatSeconds(stress.getHighStressSeconds()));
        
        // Hydration
        HydrationData hydration = garmin.getHydrationData(today);
        System.out.println("\n💧 Hydration:");
        System.out.println("  Consumed: " + hydration.getValueInML() + " ml");
        System.out.println("  Goal: " + hydration.getGoalInML() + " ml");
        System.out.println("  Progress: " + String.format("%.1f%%", 
                (double) hydration.getValueInML() / hydration.getGoalInML() * 100));
    }
    
    private static void deviceInformation() throws GarminConnectException {
        System.out.println("\n⌚ Device Information");
        System.out.println("====================\n");
        
        List<Device> devices = garmin.getDevices();
        
        if (devices.isEmpty()) {
            System.out.println("No devices found.");
            return;
        }
        
        for (Device device : devices) {
            System.out.println("Device: " + device.getDisplayName());
            System.out.println("  Product: " + device.getProductDisplayName());
            System.out.println("  Status: " + (device.isActive() ? "✓ Active" : "✗ Inactive"));
            System.out.println("  Firmware: " + device.getFirmwareVersion());
            System.out.println("  Last Sync: " + new java.util.Date(device.getLastSyncTimeGMT()));
            System.out.println();
        }
    }
    
    private static void achievementsAndRecords() throws GarminConnectException {
        System.out.println("\n🏆 Achievements & Records");
        System.out.println("=========================\n");
        
        PersonalRecords records = garmin.getPersonalRecords();
        
        if (records.getRecords().isEmpty()) {
            System.out.println("No personal records found.");
        } else {
            System.out.println("Personal Records:");
            for (PersonalRecords.PersonalRecord record : records.getRecords()) {
                System.out.println("  🏅 " + record.getRecordType() + ": " + 
                                 record.getValue() + " " + record.getUnit());
                System.out.println("     Date: " + record.getDate());
                System.out.println("     Activity: " + record.getActivityName());
                System.out.println();
            }
        }
        
        List<Badge> badges = garmin.getBadges();
        System.out.println("\nBadges (" + badges.size() + " available):");
        
        int earnedCount = 0;
        for (Badge badge : badges) {
            if (badge.isEarned()) {
                earnedCount++;
                System.out.println("  ✓ " + badge.getBadgeName() + " (Earned: " + 
                                 badge.getEarnedDate() + ")");
            }
        }
        System.out.println("\nTotal Earned: " + earnedCount + " / " + badges.size());
    }
    
    private static String formatSeconds(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        
        if (hours > 0) {
            return String.format("%dh %02dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }
}
