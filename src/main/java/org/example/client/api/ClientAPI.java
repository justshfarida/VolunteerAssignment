package org.example.client.api;

import com.google.gson.Gson;           // For JSON serialization/deserialization
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Handles communication between the client and the volunteer-matching server.
 *
 * Exposed methods (used by the UI):
 * - getAllServices()
 * - sendPreferences(id, name, prefs)
 * - triggerOptimization()
 * - setOnAssignmentReceived(callback)
 * - startPolling(id)
 */
public class ClientAPI {

    /* ------------------ Configuration ------------------ */

    // Base URL for the backend server (localhost on port 8080)
    private static final String BASE = "http://localhost:8080";

    // Shared HTTP client instance for all requests
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    // Gson instance for converting objects to/from JSON
    private static final Gson G = new Gson();

    // Polling thread that periodically fetches assignment
    private static ScheduledExecutorService poller;


    /* ------------------ Service List ------------------ */

    // Hardcoded list of available services (used for combo boxes)
    private static final String[] SERVICES = {
            "Soup Kitchen", "Animal Shelter", "Senior Care", "Airport Greeter",
            "Hackathon Mentor", "Beach Cleanup", "Community Garden", "Library Assistant",
            "Youth Mentor", "Disaster Relief"
    };

    // Return a safe clone to avoid exposing the original array
    public static String[] getAllServices() { return SERVICES.clone(); }


    /* ------------------ Assignment Callback ------------------ */

    // A callback to deliver assignment updates (set by UI)
    private static Consumer<String> callback;

    /**
     * Called by UI to register a function that will be invoked when
     * a new assignment (or related update) is received from the server.
     */
    public static void setOnAssignmentReceived(Consumer<String> cb) {
        callback = cb;
    }


    /* ------------------ Public API Methods ------------------ */

    /**
     * Submits volunteer preferences to the server (POST /preferences)
     */
    public static void sendPreferences(String id, String name, List<String> prefs) {
        Payload p = new Payload(id, name, prefs);
        String json = G.toJson(p);
        postAsync("/preferences", json, "prefs");
    }

    /**
     * Triggers server-side optimization (POST /optimize)
     */
    public static void triggerOptimization() {
        postAsync("/optimize", "", "opt");
    }

    /**
     * Optionally fetch current assignment synchronously (GET /assignment)
     */
    public static String viewAssignmentSync(String id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/assignment?volunteerId=" + id))
                .GET()
                .build();
        return HTTP.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }


    /* ------------------ Internal Helpers ------------------ */

    /**
     * Helper: Send asynchronous POST request and forward result to notify()
     */
    private static void postAsync(String path, String json, String tag) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HTTP.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> notify(tag + ": " + response.body()));
    }

    /**
     * Handles the response message and routes meaningful assignment updates to the callback
     */
    private static void notify(String json) {
        if (callback == null) return;

        // Filter out boring confirmations like "prefs: ..." or "opt: ..."
        if (json.startsWith("prefs:") || json.startsWith("opt:")) return;

        try {
            JsonObject obj = G.fromJson(json, JsonObject.class);

            // Found a real assignment result → pass to UI
            if (obj.has("assignment")) {
                callback.accept(obj.get("assignment").getAsString());
                return;
            }

            // Ignore system messages like {"error":...} or {"status":...}
            if (obj.has("error") || obj.has("status")) return;

        } catch (Exception ignore) {
            // If it's not JSON, ignore it
        }

        // Fallback (should not usually happen)
        callback.accept(json);
    }

    /**
     * Starts polling /assignment every 2 seconds and routes result to callback
     */
    public static void startPolling(String id) {
        if (poller != null) return;  // Already polling

        poller = Executors.newSingleThreadScheduledExecutor();
        poller.scheduleAtFixedRate(() -> {
            try {
                String json = viewAssignmentSync(id);  // GET assignment
                notify(json);                          // push to callback/UI
            } catch (Exception ignored) {
                // Ignore 404 errors (not assigned yet)
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    /**
     * Stops the polling thread (optional)
     */
    public static void stopPolling() {
        if (poller != null) poller.shutdownNow();
        poller = null;
    }

    /* ------------------ Internal Payload Class ------------------ */

    /**
     * Structure used for JSON payload to /preferences
     */
    private record Payload(String volunteerId, String name, List<String> prefs) {}
}
