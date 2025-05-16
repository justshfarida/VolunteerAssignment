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
 * Multithreaded HTTP server for the Volunteer Matching System,
 * plus a WebSocket server for broadcasting live assignment updates.
 *
 * REST endpoints:
 *   POST /preferences     → receive & store volunteer preferences
 *   POST /optimize        → run GA & store assignments (then broadcast via WS)
 *   GET  /assignment?volunteerId=  → return the assignment JSON or 404
 *
 * In‐memory only (no persistence).
 */
public class ServerHandler {

    /* ---------- Configuration ---------- */
    private static final int HTTP_PORT = 8080;
    private static final int WS_PORT   = 8081;
    private static final Gson G        = new Gson();

    /* ---------- In‐Memory State ---------- */
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

    // Our optimization engine – no‐arg constructor
    private static final ApplicationLogic LOGIC = new ApplicationLogic();

    // volunteerId → Volunteer (with name+prefs)
    private static final Map<String, Volunteer> VOLUNTEER_STORE =
            new ConcurrentHashMap<>();

    // volunteerId → Assignment (after optimize)
    private static final Map<String, Assignment> ASSIGNMENT_STORE =
            new ConcurrentHashMap<>();

    /* ---------- Bootstrap HTTP + WS ---------- */
    public static void main(String[] args) throws IOException {
        // 1) start HTTP server
        HttpServer http = HttpServer.create(new InetSocketAddress(HTTP_PORT), 0);
        http.createContext("/preferences", ServerHandler::handlePrefs);
        http.createContext("/optimize",    ServerHandler::handleOptimize);
        http.createContext("/assignment",  ServerHandler::handleAssignment);

        int threads = Math.max(2, Runtime.getRuntime().availableProcessors());
        http.setExecutor(Executors.newFixedThreadPool(threads));
        http.start();
        System.out.printf("HTTP server on http://localhost:%d (%d threads)%n",
                HTTP_PORT, threads);

        // 2) start WebSocket server for live broadcasts
        new AssignmentWebSocketServer(WS_PORT).start();
        System.out.printf("WebSocket server on ws://localhost:%d%n", WS_PORT);
    }

    /* ========== REST Handlers ========== */

    /** POST /preferences */
    private static void handlePrefs(HttpExchange ex) throws IOException {
        logRequest(ex);
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(405, -1);
            return;
        }

        PrefPayload p = G.fromJson(readBody(ex), PrefPayload.class);
        if (p == null
                || p.volunteerId == null
                || p.name == null
                || p.prefs == null
                || p.prefs.size() < 3) {
            sendJson(ex, 400, Map.of("error", "bad payload"));
            return;
        }

        // map names → Service objects
        Map<String, Service> byName = SERVICES.stream()
                .collect(Collectors.toMap(Service::getName, s -> s));
        List<Service> prefObjs = new ArrayList<>();
        for (String name : p.prefs) {
            Service svc = byName.get(name);
            if (svc != null) prefObjs.add(svc);
        }

        // store in‐memory; ApplicationLogic no longer holds volunteers
        VOLUNTEER_STORE.put(p.volunteerId,
                new Volunteer(p.name, p.volunteerId, prefObjs));

        sendJson(ex, 200, Map.of("status", "stored"));
    }

    /** POST /optimize */
    private static void handleOptimize(HttpExchange ex) throws IOException {
        logRequest(ex);
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(405, -1);
            return;
        }

        try {
            // gather all volunteers from the store
            List<Volunteer> allVols = new ArrayList<>(VOLUNTEER_STORE.values());

            // run the GA over deep‐copies inside ApplicationLogic
            List<Assignment> results =
                    LOGIC.runOptimization(allVols, SERVICES);

            // replace old assignments
            ASSIGNMENT_STORE.clear();
            for (Assignment a : results) {
                ASSIGNMENT_STORE.put(a.getVolunteer().getId(), a);
            }

            // broadcast each assignment via WebSocket
            for (Assignment a : results) {
                String msg = G.toJson(Map.of(
                        "volunteerId", a.getVolunteer().getId(),
                        "assignment",  a.getService().getName()
                ));
                AssignmentWebSocketServer.broadcastToAll(msg);
            }

            sendJson(ex, 200, Map.of("status", "optimized"));
        } catch (Exception e) {
            sendJson(ex, 500, Map.of("error", e.getMessage()));
        }
    }

    /** GET /assignment?volunteerId=XYZ */
    private static void handleAssignment(HttpExchange ex) throws IOException {
        logRequest(ex);
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(405, -1);
            return;
        }

        String q  = ex.getRequestURI().getQuery();
        String id = (q != null && q.startsWith("volunteerId="))
                ? q.substring("volunteerId=".length())
                : null;

        Assignment a = (id != null) ? ASSIGNMENT_STORE.get(id) : null;
        if (a == null) {
            sendJson(ex, 404, Map.of("error", "not found"));
        } else {
            sendJson(ex, 200,
                    Map.of("assignment", a.getService().getName()));
        }
    }

    /* ========== Utility Methods ========== */

    private static void logRequest(HttpExchange ex) {
        System.out.printf("🔵 %s %s%n",
                ex.getRequestMethod(), ex.getRequestURI());
    }

    private static String readBody(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private static void sendJson(HttpExchange ex,
                                 int code,
                                 Map<String,?> obj) throws IOException {
        byte[] out = G.toJson(obj).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add(
                "Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(code, out.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(out);
        }
    }

    /** JSON payload structure for /preferences */
    private record PrefPayload(
            String volunteerId,
            String name,
            List<String> prefs
    ) {}
}
