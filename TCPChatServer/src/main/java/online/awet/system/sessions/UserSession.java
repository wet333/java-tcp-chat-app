package online.awet.system.sessions;

import online.awet.system.userManagement.UserData;

import java.util.UUID;

public class UserSession implements Session {

    private final String sessionId;
    private final UserData userData;

    public UserSession() {
        sessionId = UUID.randomUUID().toString();
        userData = new UserData("testUsername");
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    public String getUsername() {
        return userData.getUsername();
    }
}
