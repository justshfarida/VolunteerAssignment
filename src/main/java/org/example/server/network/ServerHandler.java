// ------------------- 3. SERVER NETWORK -----------------------
// ServerHandler.java
package org.example.server.network;

import com.sun.net.httpserver.*;
import com.google.gson.Gson;
import org.example.domain.*;
import org.example.server.logic.ApplicationLogic;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ServerHandler {
    private static final Gson G=new Gson();
    private static final List<Service> services=List.of(
            new Service("Soup Kitchen",6), new Service("Animal Shelter",4), new Service("Senior Care",4),
            new Service("Airport Greeter",3), new Service("Hackathon Mentor",4), new Service("Beach Cleanup",6),
            new Service("Community Garden",5), new Service("Library Assistant",4), new Service("Youth Mentor",4),
            new Service("Disaster Relief",3));
    private static final ApplicationLogic logic=new ApplicationLogic(services);
    private static final Map<String,Assignment> assignments=new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        HttpServer srv=HttpServer.create(new InetSocketAddress(8080),0);
        srv.createContext("/preferences", ServerHandler::handlePrefs);
        srv.createContext("/optimize",  ServerHandler::handleOptimize);
        srv.createContext("/assignment",ServerHandler::handleAssign);
        srv.setExecutor(Executors.newFixedThreadPool(Math.max(2,Runtime.getRuntime().availableProcessors())));
        srv.start(); System.out.println("Server → http://localhost:8080");
    }

    /* ---------- handlers ---------- */
    private static void handlePrefs(HttpExchange ex) throws IOException {
        if(!ex.getRequestMethod().equalsIgnoreCase("POST")){
            ex.sendResponseHeaders(405,-1); return;
        }
        String body=new String(ex.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);
        PrefPayload p=G.fromJson(body, PrefPayload.class);
        if(p==null||p.volunteerId==null||p.prefs==null||p.prefs.size()<3)
        { send(ex,400,"{\"error\":\"bad payload\"}"); return; }
        Map<String,Service> map=services.stream().collect(Collectors.toMap(Service::getName,s->s));
        List<Service> prefs=new ArrayList<>();
        p.prefs.forEach(n->{ if(map.containsKey(n)) prefs.add(map.get(n)); });
        logic.addVolunteer(new Volunteer(p.volunteerId,p.name , prefs));
        send(ex,200,"{\"status\":\"stored\"}");
    }

    private static void handleOptimize(HttpExchange ex) throws IOException {
        if(!ex.getRequestMethod().equalsIgnoreCase("POST")){ ex.sendResponseHeaders(405,-1);return; }
        List<Assignment> res=logic.runOptimization(); assignments.clear(); res.forEach(a->assignments.put(a.getVolunteer().getId(),a));
        send(ex,200,"{\"status\":\"optimized\"}");
    }

    private static void handleAssign(HttpExchange ex) throws IOException {
        if(!ex.getRequestMethod().equalsIgnoreCase("GET")){ ex.sendResponseHeaders(405,-1);return; }
        String q=ex.getRequestURI().getQuery(); String id=q!=null&&q.startsWith("volunteerId=")?q.substring(12):null;
        Assignment a=id!=null?assignments.get(id):null;
        if(a==null){ send(ex,404,"{\"error\":\"not found\"}"); return; }
        send(ex,200,G.toJson(Map.of("assignment",a.getService().getName())));
    }

    /* ---------- helpers ---------- */
    private static void send(HttpExchange ex,int code,String json) throws IOException {
        byte[] b=json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type","application/json; charset=utf-8");
        ex.sendResponseHeaders(code,b.length); try(OutputStream os=ex.getResponseBody()){ os.write(b);} }
    private record PrefPayload(String volunteerId, String name, List<String> prefs) {}


}