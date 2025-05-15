package org.example.server.network;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Web-socket endpoint:   ws://localhost:8081
 */
public class AssignmentWebSocketServer extends WebSocketServer {

    /* -------------------------------------------------- */
    /** singleton reference so other classes can reach us */
    public  static AssignmentWebSocketServer instance;

    /** every open socket */
    private static final Set<WebSocket> peers = new CopyOnWriteArraySet<>();

    /* -------------------------------------------------- */
    public AssignmentWebSocketServer(int port) {
        super(new InetSocketAddress(port));
        instance = this;                // <-- keep global handle
    }

    @Override public void onOpen (WebSocket c, ClientHandshake h){ peers.add(c); }
    @Override public void onClose(WebSocket c,int code,String r,boolean remote){ peers.remove(c); }
    @Override public void onError(WebSocket c,Exception ex){ ex.printStackTrace(); }
    @Override public void onMessage(WebSocket c,String msg){ /* server is write-only */ }
    @Override public void onStart(){ System.out.println("✅ WS server on :8081"); }

    /* -------------------------------------------------- */
    /** Broadcast helper the HTTP handlers can call. */
    public static void broadcastToAll(String msg){
        // no server yet? => nothing to do
        if (instance == null) return;

        peers.forEach(ws -> { if (ws.isOpen()) ws.send(msg); });
    }
}
