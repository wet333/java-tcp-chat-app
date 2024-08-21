package online.awet.system.broadcast;

import online.awet.threads.ClientHandlerThread;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BroadcastManager {

    private static final BroadcastManager instance = new BroadcastManager(); // No lazy loading, init with app.
    private final Set<ClientHandlerThread> broadcastMembers = Collections.synchronizedSet(new HashSet<ClientHandlerThread>());

    // Broadcast Config
    private static final boolean SELF_BROADCAST = true;

    private BroadcastManager() {}

    public static BroadcastManager getInstance() {
        return instance;
    }

    public void addBroadcastMember(ClientHandlerThread member) {
        broadcastMembers.add(member);
    }

    public void removeBroadcastMember(ClientHandlerThread member) {
        broadcastMembers.remove(member);
    }

    public void broadcast(String message, ClientHandlerThread sender) {
        synchronized (broadcastMembers) {
            for (ClientHandlerThread member : broadcastMembers) {
                if (member.equals(sender)) {
                    if (SELF_BROADCAST) {
                        // Add a * to indicate that the message is from the sender itself
                        member.sendMessageToClient("* " + message);
                    }
                    continue;
                }
                member.sendMessageToClient(message);
            }
        }
    }
}
