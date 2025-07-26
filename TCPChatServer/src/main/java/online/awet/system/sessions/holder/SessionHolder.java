package online.awet.system.sessions.holder;

import online.awet.system.sessions.Session;

import java.util.Map;

public interface SessionHolder {

    Session getCurrentSession();

    void deleteSession();

    void authenticate(Map<String, String> userInfo);
}