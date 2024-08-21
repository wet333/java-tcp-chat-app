package online.awet.system.broadcast;

import online.awet.system.sessions.Session;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BroadcastManager {

    private static final BroadcastManager instance = new BroadcastManager(); // No lazy loading, init with app.
    private final Set<BroadcastMember> broadcastMembers = Collections.synchronizedSet(new HashSet<>());

    private BroadcastManager() {}

    public static BroadcastManager getInstance() {
        return instance;
    }

    public void addBroadcastMember(Session session, BufferedWriter member) {
        broadcastMembers.add(new BroadcastMember(session.getSessionId(), member));
    }

    public void removeBroadcastMember(Session session) {
        broadcastMembers.removeIf(member -> member.getSessionId().equals(session.getSessionId()));
    }

    // TODO: In following updates I will need to implement the following combinations:
    // Server -> All, Server -> Room, Server -> Session
    // Session -> All, Session -> Room, Session -> Session
    // Also i will need to manage GuestSessions and LoggedSessions separately

    public void sendTo(String message, Session session) {
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
