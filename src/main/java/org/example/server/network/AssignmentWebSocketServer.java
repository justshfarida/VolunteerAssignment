package org.example.server.network;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket endpoint for broadcasting assignment updates to all connected clients.
 * Listens on ws://localhost:8081
 */
public class AssignmentWebSocketServer extends WebSocketServer {

    /**
     * Singleton instance reference so that HTTP handlers (in ServerHandler)
     * can access this server to broadcast messages.
     */
    public static AssignmentWebSocketServer instance;

    /**
     * Thread-safe set of all currently open WebSocket connections (peers).
     * CopyOnWriteArraySet allows safe iteration/broadcast without explicit locking.
     */
    private static final Set<WebSocket> peers = new CopyOnWriteArraySet<>();

    /**
     * Constructor: binds the WebSocketServer to the given port
     * and sets the static instance reference.
     *
     * @param port the TCP port to listen on (e.g. 8081)
     */
    public AssignmentWebSocketServer(int port) {
        super(new InetSocketAddress(port));
        instance = this;  // store global handle for static access elsewhere
    }

    /**
     * Called when a new client successfully opens a WebSocket connection.
     * We add the WebSocket to our peers set so we can broadcast to it later.
     */
    @Override
    public void onOpen(WebSocket socket, ClientHandshake handshake) {
        peers.add(socket);
    }

    /**
     * Called when a client connection is closed (cleanly or abnormally).
     * We remove it from our peers set.
     */
    @Override
    public void onClose(WebSocket socket, int code, String reason, boolean remote) {
        peers.remove(socket);
    }

    /**
     * Called when an error occurs on a connection.
     * We simply log the stack trace.
     */
    @Override
    public void onError(WebSocket socket, Exception ex) {
        ex.printStackTrace();
    }

    /**
     * Called when a message is received from a client.
     * In this application we don't expect inbound messages, so this is a no-op.
     */
    @Override
    public void onMessage(WebSocket socket, String message) {
        // Server is write-only; ignore any client messages
    }

    /**
     * Called once the server has been set up and is ready to accept connections.
     * We print a confirmation to standard output.
     */
    @Override
    public void onStart() {
        System.out.println("✅ WS server on :8081");
    }

    /**
     * Broadcast a text message to all connected clients.
     * HTTP handlers should call this method whenever an assignment update
     * needs to be pushed out in real-time.
     *
     * @param msg the JSON-encoded message to send
     */
    public static void broadcastToAll(String msg) {
        // If the server hasn't been instantiated yet, there's nothing to broadcast
        if (instance == null) return;

        // Send to each open WebSocket
        for (WebSocket ws : peers) {
            if (ws.isOpen()) {
                ws.send(msg);
            }
        }
    }
}
