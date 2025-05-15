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
 * Simple multithreaded HTTP server:
 *   POST /preferences   -> store or update volunteer preferences
 *   POST /optimize      -> run genetic algorithm, produce assignments
 *   GET  /assignment?volunteerId=ID -> {"assignment":"Service"} or 404
 *
 * Server runs entirely in RAM (no DB).
 */
public class ServerHandler {

    /* ---------- configuration ---------- */
    private static final int PORT = 8080;
    private static final Gson G   = new Gson();

    /* ---------- in‑memory state ---------- */
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

    private static final ApplicationLogic LOGIC =
            new ApplicationLogic(SERVICES);

    private static final Map<String, Assignment> ASSIGNMENT_STORE =
            new ConcurrentHashMap<>();

    /* ---------- server bootstrap ---------- */
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/preferences", ServerHandler::handlePrefs);
        server.createContext("/optimize",    ServerHandler::handleOptimize);
        server.createContext("/assignment",  ServerHandler::handleAssignment);

        int threads = Math.max(2, Runtime.getRuntime().availableProcessors());
        server.setExecutor(Executors.newFixedThreadPool(threads));
        server.start();
        System.out.printf("Server running on http://localhost:%d (%d threads)%n",
                PORT, threads);
    }

    /* ====================== HANDLERS ========================== */

    /** POST /preferences  JSON: {volunteerId, name, prefs:[...]}  */
    private static void handlePrefs(HttpExchange ex) throws IOException {
        System.out.println("🔵 " + ex.getRequestMethod() + " " + ex.getRequestURI());

        if (!ex.getRequestMethod().equalsIgnoreCase("POST")) {
            ex.sendResponseHeaders(405, -1); return;
        }

        PrefPayload p = G.fromJson(readBody(ex), PrefPayload.class);
        if (p == null || p.volunteerId == null ||
                p.name == null || p.prefs == null || p.prefs.size() < 3) {
            send(ex, 400, "{\"error\":\"bad payload\"}");  return;
        }

        /* map service names -> objects */
        Map<String, Service> byName =
                SERVICES.stream().collect(Collectors.toMap(Service::getName, s -> s));

        List<Service> prefObjs = new ArrayList<>();
        p.prefs.forEach(n -> { if (byName.containsKey(n)) prefObjs.add(byName.get(n)); });

        LOGIC.addVolunteer(new Volunteer(p.name, p.volunteerId, prefObjs));
        send(ex, 200, "{\"status\":\"stored\"}");
    }

    /** POST /optimize  -> runs GA & stores assignments  */
    private static void handleOptimize(HttpExchange ex) throws IOException {
        System.out.println("🔵 " + ex.getRequestMethod() + " " + ex.getRequestURI());

        if (!ex.getRequestMethod().equalsIgnoreCase("POST")) {
            ex.sendResponseHeaders(405, -1); return;
        }
        try {
            List<Assignment> result = LOGIC.runOptimization();
            ASSIGNMENT_STORE.clear();
            result.forEach(a -> ASSIGNMENT_STORE.put(a.getVolunteer().getId(), a));
            send(ex, 200, "{\"status\":\"optimized\"}");
        } catch (Exception e) {
            send(ex, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /** GET /assignment?volunteerId=abc  */
    private static void handleAssignment(HttpExchange ex) throws IOException {
        System.out.println("🔵 " + ex.getRequestMethod() + " " + ex.getRequestURI());

        if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
            ex.sendResponseHeaders(405, -1);  return;
        }
        String q = ex.getRequestURI().getQuery();
        String id = (q != null && q.startsWith("volunteerId=")) ? q.substring(12) : null;

        Assignment a = (id != null) ? ASSIGNMENT_STORE.get(id) : null;
        if (a == null) { send(ex, 404, "{\"error\":\"not found\"}"); return; }

        send(ex, 200, G.toJson(Map.of("assignment", a.getService().getName())));
    }

    /* ====================== UTILS ============================= */
    private static String readBody(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static void send(HttpExchange ex, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    /* JSON payload for /preferences */
    private record PrefPayload(String volunteerId, String name, List<String> prefs) {}
}
