package online.awet.system.sessions.users;

import java.util.UUID;

public class Guest {

    public static String autogenerateGuestId() {
        return UUID.randomUUID().toString();
    }
}
