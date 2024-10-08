package online.awet.system.broadcast;

import online.awet.system.sessions.Session;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Singleton class BroadcastManager handles all broadcasting functionalities within the chat system.
 * It maintains a list of active broadcast members, enabling message dispatch to specific sessions,
 * individual clients, or all connected users.
 *
 * <p>
 * Key responsibilities include managing members, sending targeted messages, and
 * broadcasting server-wide notifications.
 * </p>
 *
 * <p>
 * Future updates planned for BroadcastManager:
 * <ul>
 *     <li>Server-to-specific-room and Server-to-specific-session messaging</li>
 *     <li>Session-based broadcasting, allowing both session-to-all and session-to-session communication</li>
 *     <li>Separate management of guest and logged-in sessions</li>
 * </ul>
 * </p>
 *
 * @see Session
 */
public class BroadcastManager {

    private static final BroadcastManager instance = new BroadcastManager(); // No lazy loading, init with app.

    /**
     * Thread-safe set of broadcast members that holds the active sessions capable of receiving broadcasts.
     */
    private final Set<BroadcastMember> broadcastMembers = Collections.synchronizedSet(new HashSet<>());

    // Private constructor for singleton instantiation
    private BroadcastManager() {}

    /**
     * Provides global access to the singleton instance of BroadcastManager.
     *
     * @return The singleton instance of BroadcastManager.
     */
    public static BroadcastManager getInstance() {
        return instance;
    }

    /**
     * Adds a new member to the broadcast list. Each member is associated with a unique session ID
     * and a BufferedWriter for sending messages.
     *
     * @param session The session object representing the client session.
     * @param member The BufferedWriter associated with the client for message sending.
     */
    public void addBroadcastMember(Session session, BufferedWriter member) {
        broadcastMembers.add(new BroadcastMember(session.getSessionId(), member));
    }

    /**
     * Removes a member from the broadcast list, based on their session ID.
     *
     * @param session The session to be removed from the broadcast list.
     */
    public void removeBroadcastMember(Session session) {
        broadcastMembers.removeIf(member -> member.getSessionId().equals(session.getSessionId()));
    }

    // TODO: In following updates I will need to implement the following combinations:
    // Server -> All, Server -> Room, Server -> Session
    // Session -> All, Session -> Room, Session -> Session
    // Also I will need to manage GuestSessions and LoggedSessions separately

    /**
     * Broadcasts a message from a specific session to all connected clients.
     * The message appears prefixed with an asterisk(*) if sent to the originating session.
     *
     * @param message The message to broadcast.
     * @param session The session originating the broadcast.
     */
    public void broadcast(String message, Session session) {
        synchronized (broadcastMembers) {
            for (BroadcastMember member : broadcastMembers) {
                try {
                    String messageToSend = member.getSessionId().equals(session.getSessionId()) ? "* " + message : message;
                    member.getWriter().write(messageToSend);
                    member.getWriter().newLine();
                    member.getWriter().flush();

                } catch (IOException e) {
                    System.out.println("Couldn't send message <<" + message + ">> to client. Cause: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Sends a direct message from the server to a specific client session.
     * Only the client identified by the provided session ID receives this message,
     * which is prefixed with "Server:" to indicate its origin.
     *
     * @param message The message to send to the client.
     * @param session The target client session for the message.
     */
    public void serverDirectMessage(String message, Session session) {
        synchronized (broadcastMembers) {
            for (BroadcastMember member : broadcastMembers) {
                try {
                    if (member.getSessionId().equals(session.getSessionId())) {
                        member.getWriter().write("Server: " + message);
                        member.getWriter().newLine();
                        member.getWriter().flush();
                        break;
                    }
                } catch (IOException e) {
                    System.out.println("Couldn't send message <<" + message + ">> to client. Cause: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Sends a server-originated broadcast message to all connected clients.
     * The message is prefixed with "Server:" to indicate it is sent by the server.
     *
     * @param message The message to broadcast to all clients.
     */
    public void serverBroadcast(String message) {
        synchronized (broadcastMembers) {
            for (BroadcastMember member : broadcastMembers) {
                try {
                    message = "Server: " + message;
                    member.getWriter().write(message);
                    member.getWriter().newLine();
                    member.getWriter().flush();

                } catch (IOException e) {
                    System.out.println("Couldn't send message <<" + message + ">> to client. Cause: " + e.getMessage());
                }
            }
        }
    }
}
