// ------------------- 4. CLIENT API ---------------------------
// ClientAPI.java
package org.example.client.api;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.*;
import java.util.List;
import java.util.function.Consumer;
public class ClientAPI {
    private static final String BASE="http://localhost:8080";
    private static final HttpClient HTTP=HttpClient.newHttpClient();
    private static final Gson G=new Gson();
    private static Consumer<String> cb;
    private static final String[] SERVICES={"Soup Kitchen","Animal Shelter","Senior Care","Airport Greeter","Hackathon Mentor","Beach Cleanup","Community Garden","Library Assistant","Youth Mentor","Disaster Relief"};
    public static String[] getAllServices(){ return SERVICES.clone(); }
    public static void setOnAssignmentReceived(Consumer<String> c){ cb=c; }
    private static void post(String path,String json,String tag){
        HttpRequest req=HttpRequest.newBuilder().uri(URI.create(BASE+path)).header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HTTP.sendAsync(req,HttpResponse.BodyHandlers.ofString()).thenAccept(r->notify(tag+": "+r.body())); }
    // overload the method — keep the old id‑only version if you still need it
    public static void sendPreferences(String id, String name, List<String> prefs) {
        Payload p = new Payload(id, name, prefs);
        post("/preferences", G.toJson(p), "Prefs ack");
    }

    /* update record */
    private record Payload(String volunteerId, String name, List<String> prefs) {}

    public static void triggerOptimization(){ post("/optimize","","Optimize ack"); }
    public static String viewAssignmentSync(String id) throws Exception {
        HttpRequest req=HttpRequest.newBuilder().uri(URI.create(BASE+"/assignment?volunteerId="+id)).GET().build();
        return HTTP.send(req,HttpResponse.BodyHandlers.ofString()).body(); }
    private static void notify(String m)
    {
        if(cb!=null)
            cb.accept(m);
    }
}

