package online.awet.server;

import online.awet.ConnectionHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BroadcastManager {

    private static final BroadcastManager instance = new BroadcastManager();
    private final Set<ConnectionHandler> broadcastMembers = Collections.synchronizedSet(new HashSet<ConnectionHandler>());

    // Broadcast Config
    private static final boolean SELF_BROADCAST = true;

    private BroadcastManager() {}

    public static BroadcastManager getInstance() {
        return instance;
    }

    public void addBroadcastMember(ConnectionHandler member) {
        broadcastMembers.add(member);
    }

    public void removeBroadcastMember(ConnectionHandler member) {
        broadcastMembers.remove(member);
    }

    public void broadcast(String message, ConnectionHandler sender) {
        synchronized (broadcastMembers) {
            for (ConnectionHandler member : broadcastMembers) {
                if (!SELF_BROADCAST && member.equals(sender)) {
                    continue;
                }
                member.sendMessageToClient(message);
            }
        }
    }
}
