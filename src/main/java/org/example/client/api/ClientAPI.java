package org.example.client.api;

import com.google.gson.Gson; //Json serialization
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
 * Talk to the Volunteer‑Matching server.
 * UI depends on: getAllServices(), sendPreferences(id,name,prefs), triggerOptimization(),
 *                setOnAssignmentReceived(callback)
 */
public class ClientAPI {

    /* ---------------- configuration ---------------- */
    private static final String BASE = "http://localhost:8080"; // – root URL of your server.


    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final Gson G = new Gson();
    private static ScheduledExecutorService poller;


    /* ---------------- static service list ---------------- */
    private static final String[] SERVICES = {
            "Soup Kitchen", "Animal Shelter", "Senior Care", "Airport Greeter",
            "Hackathon Mentor", "Beach Cleanup", "Community Garden", "Library Assistant",
            "Youth Mentor", "Disaster Relief"
    };
    public static String[] getAllServices() { return SERVICES.clone(); }

    /* ---------------- async callback ---------------- */
    private static Consumer<String> callback;
    public static void setOnAssignmentReceived(Consumer<String> cb)
    { callback = cb; }

    /* --------------------------------------------------- */
    /*          PUBLIC METHODS USED BY THE UI              */
    /* --------------------------------------------------- */

    /** POST /preferences  */
    public static void sendPreferences(String id, String name, List<String> prefs) {
        Payload p = new Payload(id, name, prefs); //Serialises to JSON with Gson.
        postAsync("/preferences", G.toJson(p), "prefs");
    }

    /** POST /optimize  */
    public static void triggerOptimization() {
        postAsync("/optimize", "", "opt");
    }

    /** Optionally: synchronous fetch (unused in current UI) */
    public static String viewAssignmentSync(String id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/assignment?volunteerId=" + id))
                .GET().build();
        return HTTP.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }

    /* --------------------------------------------------- */
    /*                 INTERNAL HELPERS                    */
    /* --------------------------------------------------- */

    private static void postAsync(String path, String json, String tag) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HTTP.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenAccept(r -> notify(tag + ": " + r.body()));
    }

    private static void notify(String json) {
        if (callback == null) return;

        /* ▼  Skip ACK messages coming from POST /preferences or /optimize */
        if (json.startsWith("prefs:") || json.startsWith("opt:"))
            return;

        try {
            JsonObject obj = G.fromJson(json, JsonObject.class);

            if (obj.has("assignment")) {                   // success
                callback.accept(obj.get("assignment").getAsString());
                return;
            }
            if (obj.has("error")  || obj.has("status"))    // ignore 404 + status
                return;
        } catch (Exception ignore) { /* not JSON */ }

        /* fallback – should never happen now */
        callback.accept(json);
    }

    /**—> polls /assignment every 2 s and notifies callback */
    public static void startPolling(String id) {
        if (poller != null) return;                       // already running
        poller = Executors.newSingleThreadScheduledExecutor();
        poller.scheduleAtFixedRate(() -> {
            try {
                String json = viewAssignmentSync(id);     // GET /assignment
                notify(json);                             // push to UI
            } catch (Exception ignored) { /* 404 until optimized */ }
        }, 0, 2, TimeUnit.SECONDS);
    }

    /** stop polling when window closes (optional call from UI) */
    public static void stopPolling() {
        if (poller != null) poller.shutdownNow();
        poller = null;
    }
    /* JSON payload for sendPreferences */
    private record Payload(String volunteerId, String name, List<String> prefs) {}
}
