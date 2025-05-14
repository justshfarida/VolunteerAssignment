package org.example.client.api;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

/**
 * Talk to the Volunteer‑Matching server.
 * UI depends on: getAllServices(), sendPreferences(id,name,prefs), triggerOptimization(),
 *                setOnAssignmentReceived(callback)
 */
public class ClientAPI {

    /* ---------------- configuration ---------------- */
    private static final String BASE = "http://localhost:8080";
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final Gson G = new Gson();

    /* ---------------- static service list ---------------- */
    private static final String[] SERVICES = {
            "Soup Kitchen", "Animal Shelter", "Senior Care", "Airport Greeter",
            "Hackathon Mentor", "Beach Cleanup", "Community Garden", "Library Assistant",
            "Youth Mentor", "Disaster Relief"
    };
    public static String[] getAllServices() { return SERVICES.clone(); }

    /* ---------------- async callback ---------------- */
    private static Consumer<String> callback;
    public static void setOnAssignmentReceived(Consumer<String> cb) { callback = cb; }

    /* --------------------------------------------------- */
    /*          PUBLIC METHODS USED BY THE UI              */
    /* --------------------------------------------------- */

    /** POST /preferences  */
    public static void sendPreferences(String id, String name, List<String> prefs) {
        Payload p = new Payload(id, name, prefs);
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

    private static void notify(String txt) {
        if (callback != null) callback.accept(txt);
    }

    /* JSON payload for sendPreferences */
    private record Payload(String volunteerId, String name, List<String> prefs) {}
}
