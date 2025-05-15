package org.example.server.network;

import com.sun.net.httpserver.*;
import com.google.gson.Gson;
import org.example.domain.*;
import org.example.server.logic.ApplicationLogic;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Multithreaded HTTP server for the Volunteer Matching System.
 *
 * Supports three endpoints:
 * - POST /preferences     → receive and store volunteer preferences
 * - POST /optimize        → run the genetic algorithm and compute assignments
 * - GET  /assignment?id=  → return the assigned service (if any)
 *
 * In-memory, no database is used.
 */
public class ServerHandler {

    /* ---------- Configuration ---------- */

    private static final int PORT = 8080;           // server port
    private static final Gson G = new Gson();       // JSON utility

    /* ---------- Static Data (In-Memory State) ---------- */

    // Fixed list of service types and capacities
    private static final List<Service> SERVICES = List.of(
            new Service("Soup Kitchen",    6),
            new Service("Animal Shelter",  4),
            new Service("Senior Care",     4),
            new Service("Airport Greeter", 3),
            new Service("Hackathon Mentor",4),
            new Service("Beach Cleanup",   6),
            new Service("Community Garden",5),
            new Service("Library Assistant",4),
            new Service("Youth Mentor",    4),
            new Service("Disaster Relief", 3)
    );

    // Optimization logic module
    private static final ApplicationLogic LOGIC = new ApplicationLogic();

    // Maps volunteer ID → final assignment (after optimization)
    private static final Map<String, Assignment> ASSIGNMENT_STORE = new ConcurrentHashMap<>();

    // Maps volunteer ID → volunteer object (with name + preferences)
    private static final Map<String, Volunteer> VOLUNTEER_STORE = new ConcurrentHashMap<>();


    /* ---------- Server Bootstrap ---------- */

    public static void main(String[] args) throws IOException {
        // Create HTTP server instance
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Register endpoints
        server.createContext("/preferences", ServerHandler::handlePrefs);
        server.createContext("/optimize",    ServerHandler::handleOptimize);
        server.createContext("/assignment",  ServerHandler::handleAssignment);

        // Multi-threaded pool for concurrent clients
        int threads = Math.max(2, Runtime.getRuntime().availableProcessors());
        server.setExecutor(Executors.newFixedThreadPool(threads));

        // Start server
        server.start();
        System.out.printf("Server running on http://localhost:%d (%d threads)%n", PORT, threads);
    }

    /* =================== ENDPOINT HANDLERS =================== */

    /**
     * POST /preferences
     * Receives a JSON body with volunteerId, name, and a list of preferences.
     * Example payload:
     * {
     *   "volunteerId": "vol123",
     *   "name": "Alice",
     *   "prefs": ["Soup Kitchen", "Library Assistant", "Senior Care"]
     * }
     */
    private static void handlePrefs(HttpExchange ex) throws IOException {
        System.out.println("🔵 " + ex.getRequestMethod() + " " + ex.getRequestURI());

        // Only allow POST
        if (!ex.getRequestMethod().equalsIgnoreCase("POST")) {
            ex.sendResponseHeaders(405, -1); return;
        }

        // Parse JSON payload into PrefPayload
        PrefPayload p = G.fromJson(readBody(ex), PrefPayload.class);
        if (p == null || p.volunteerId == null || p.name == null || p.prefs == null || p.prefs.size() < 3) {
            send(ex, 400, "{\"error\":\"bad payload\"}"); return;
        }

        // Convert string preferences → Service objects
        Map<String, Service> byName = SERVICES.stream()
                .collect(Collectors.toMap(Service::getName, s -> s));
        List<Service> prefObjs = new ArrayList<>();
        for (String name : p.prefs) {
            if (byName.containsKey(name)) prefObjs.add(byName.get(name));
        }

        // Store the volunteer in memory
        VOLUNTEER_STORE.put(p.volunteerId, new Volunteer(p.name, p.volunteerId, prefObjs));
        send(ex, 200, "{\"status\":\"stored\"}");
    }

    /**
     * POST /optimize
     * Runs the genetic algorithm with all stored volunteers and services,
     * then stores the resulting assignments.
     */
    private static void handleOptimize(HttpExchange ex) throws IOException {
        System.out.println("🔵 " + ex.getRequestMethod() + " " + ex.getRequestURI());

        // Only allow POST
        if (!ex.getRequestMethod().equalsIgnoreCase("POST")) {
            ex.sendResponseHeaders(405, -1); return;
        }

        try {
            List<Volunteer> allVolunteers = new ArrayList<>(VOLUNTEER_STORE.values());

            // Run the optimization
            List<Assignment> result = LOGIC.runOptimization(allVolunteers, SERVICES);

            // Clear old assignments and store new ones
            ASSIGNMENT_STORE.clear();
            for (Assignment a : result) {
                ASSIGNMENT_STORE.put(a.getVolunteer().getId(), a);
            }

            send(ex, 200, "{\"status\":\"optimized\"}");
        } catch (Exception e) {
            send(ex, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * GET /assignment?volunteerId=XYZ
     * Looks up the assignment for the given ID and returns:
     *  - {"assignment": "Service Name"} if found
     *  - 404 {"error": "not found"} if not
     */
    private static void handleAssignment(HttpExchange ex) throws IOException {
        System.out.println("🔵 " + ex.getRequestMethod() + " " + ex.getRequestURI());

        // Only allow GET
        if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
            ex.sendResponseHeaders(405, -1); return;
        }

        // Parse volunteerId from URL query
        String q = ex.getRequestURI().getQuery();
        String id = (q != null && q.startsWith("volunteerId=")) ? q.substring(12) : null;

        // Lookup assignment by ID
        Assignment a = (id != null) ? ASSIGNMENT_STORE.get(id) : null;
        if (a == null) {
            send(ex, 404, "{\"error\":\"not found\"}");
            return;
        }

        // Return assigned service
        send(ex, 200, G.toJson(Map.of("assignment", a.getService().getName())));
    }

    /* =================== UTILITY METHODS =================== */

    /**
     * Reads the full request body and returns it as a UTF-8 string
     */
    private static String readBody(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Sends a JSON response with the given HTTP status code
     */
    private static void send(HttpExchange ex, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    /* Payload structure used for /preferences POST endpoint */
    private record PrefPayload(String volunteerId, String name, List<String> prefs) {}
}
