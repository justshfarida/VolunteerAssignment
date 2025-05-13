package org.example.client.api;

import java.util.List;
import java.util.function.Consumer;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;

public class ClientAPI {
    private static Consumer<String> assignmentCallback;
    private static final List<String> lastPreferences = new CopyOnWriteArrayList<>();
    private static final Random random = new Random();
    
    private static final String[] ALL_SERVICES = {
        "Soup Kitchen", "Animal Shelter", "Senior Care",
        "Airport Greeter", "Hackathon Mentor", "Beach Cleanup",
        "Community Garden", "Library Assistant", "Youth Mentor",
        "Disaster Relief", "Food Bank", "Homeless Shelter",
        "Park Maintenance", "Tutoring", "Hospital Volunteer"
    };

    // Method to submit preferences
    public static synchronized void submitPreferences(List<String> preferences) {
        if (preferences == null || preferences.size() < 3) {
            sendError("At least 3 unique preferences required");
            return;
        }

        // Check for duplicates
        if (preferences.stream().distinct().count() != preferences.size()) {
            sendError("Duplicate preferences detected");
            return;
        }

        lastPreferences.clear();
        lastPreferences.addAll(preferences);
        sendResponse("Preferences saved successfully");
    }

    // Method to trigger optimization and assign a service based on preferences
    public static synchronized void triggerOptimization() {
        if (lastPreferences.isEmpty()) {
            sendError("No preferences available for optimization");
            return;
        }

        // Weighted random selection favoring higher preferences
        double rand = random.nextDouble();
        int index;
        
        if (rand < 0.55) index = 0;          // 55% chance for 1st preference
        else if (rand < 0.85) index = 1;     // 30% chance for 2nd preference
        else if (rand < 0.95) index = 2;     // 10% chance for 3rd preference
        else if (lastPreferences.size() > 3)  // 5% chance for remaining
            index = 3 + random.nextInt(lastPreferences.size() - 3);
        else 
            index = random.nextInt(lastPreferences.size());

        sendResponse(lastPreferences.get(index));
    }

    // Method to fetch assignments (returns the last preferences)
    public static List<String> fetchAssignments() {
        return new ArrayList<>(lastPreferences);
    }

    // Method to set the callback for assignments
    public static void setOnAssignmentReceived(Consumer<String> callback) {
        assignmentCallback = callback;
    }

    // Method to get all available services
    public static String[] getAllServices() {
        return ALL_SERVICES.clone();
    }

    // Private method to send response asynchronously
    private static void sendResponse(String message) {
        new Thread(() -> {
            try {
                // Simulate processing delay
                Thread.sleep(800 + random.nextInt(1200));
                if (assignmentCallback != null) {
                    assignmentCallback.accept(message);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    // Private method to send error messages
    private static void sendError(String error) {
        if (assignmentCallback != null) {
            assignmentCallback.accept("Error: " + error);
        }
    }
}
