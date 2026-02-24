package online.awet.system.broadcast;

import online.awet.system.sessions.Session;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BroadcastManager {

    private static final BroadcastManager instance = new BroadcastManager();

    private final Set<BroadcastMember> broadcastMembers = Collections.synchronizedSet(new HashSet<>());

    private static final String SERVER_PREFIX = "";
    private static final String USER_PREFIX = "> ";

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


    public void broadcast(String message, Session session) {
        synchronized (broadcastMembers) {
            for (BroadcastMember member : broadcastMembers) {
                try {
                    String messageToSend = member.getSessionId().equals(session.getSessionId()) ? USER_PREFIX + message : message;
                    member.getWriter().write(messageToSend);
                    member.getWriter().newLine();
                    member.getWriter().flush();

                } catch (IOException e) {
                    System.out.println("Couldn't send message <<" + message + ">> to client. Cause: " + e.getMessage());
                }
            }
        }
    }

    public void serverDirectMessage(String message, Session session) {
        synchronized (broadcastMembers) {
            for (BroadcastMember member : broadcastMembers) {
                try {
                    if (member.getSessionId().equals(session.getSessionId())) {
                        member.getWriter().write(SERVER_PREFIX + message);
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

    public void serverBroadcast(String message) {
        String formattedMessage = SERVER_PREFIX + message;
        synchronized (broadcastMembers) {
            for (BroadcastMember member : broadcastMembers) {
                try {
                    member.getWriter().write(formattedMessage);
                    member.getWriter().newLine();
                    member.getWriter().flush();
                } catch (IOException e) {
                    System.out.println("Couldn't send message <<" + formattedMessage + ">> to client. Cause: " + e.getMessage());
                }
            }
        }
    }
}
