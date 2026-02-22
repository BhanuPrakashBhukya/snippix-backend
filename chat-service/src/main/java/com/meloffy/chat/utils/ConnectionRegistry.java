package com.meloffy.chat.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionRegistry {

    // userId -> active WS sessions
    private final Map<String, Set<WebSocketSession>> userSessions =
            new ConcurrentHashMap<>();

    /* =========================
       BIND / UNBIND
       ========================= */

    public boolean bindUser(String userId, WebSocketSession session) {
        Set<WebSocketSession> sessions =
                userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet());

        cleanupClosedSessions(sessions);

        boolean wasOffline = sessions.isEmpty();
        sessions.add(session);

        return wasOffline;
    }

    public boolean unbindUser(String userId, WebSocketSession session) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions == null) return false;

        sessions.remove(session);
        cleanupClosedSessions(sessions);

        if (sessions.isEmpty()) {
            userSessions.remove(userId);
            return true;
        }

        return false;
    }

    /* =========================
       QUERIES
       ========================= */

    public boolean isUserOnline(String userId) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions == null) return false;

        cleanupClosedSessions(sessions);
        return !sessions.isEmpty();
    }

    public Set<WebSocketSession> getSessions(String userId) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions == null) return Set.of();

        cleanupClosedSessions(sessions);
        return sessions;
    }

    public Collection<Set<WebSocketSession>> getAllSessions() {
        return userSessions.values();
    }

    public Set<String> getOnlineUserIds() {
        userSessions.forEach((u, s) -> cleanupClosedSessions(s));
        return userSessions.keySet();
    }

    public void removeUser(String userId) {
        userSessions.remove(userId);
    }

    private void cleanupClosedSessions(Set<WebSocketSession> sessions) {
        sessions.removeIf(s -> !s.isOpen());
    }
}