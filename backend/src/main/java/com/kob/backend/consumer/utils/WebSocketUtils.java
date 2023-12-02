package com.kob.backend.consumer.utils;

import com.kob.backend.consumer.Game;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.User;

import javax.jws.soap.SOAPBinding;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class WebSocketUtils {
    private static class WsSessionManager {
        final ConcurrentHashMap<Object, Session> sessionPool = new ConcurrentHashMap<>();
        void addSession(Object key, Session session) {
            sessionPool.put(key, session);
        }
        void removeSession(Object key) {
            Session session = sessionPool.remove(key);
            try {
                if (session.isOpen()) {
                    session.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        boolean containsSession(Object key) {
            return sessionPool.containsKey(key);
        }
        Session getSession(Object key) {
            return sessionPool.get(key);
        }
        
    }
    private static final WsSessionManager sessionManager = new WsSessionManager(); // 管理Session
    private static final CopyOnWriteArraySet<User> matchPool = new CopyOnWriteArraySet<>(); // 管理匹配用户
    private static final ConcurrentHashMap<Integer, WebSocketServer> socketPool = new ConcurrentHashMap<>(); // 管理游戏
    private WebSocketUtils() {}
    
    public static void addSession(Object key, Session session) {
        sessionManager.addSession(key, session);
    }
    public static void addSocket(Integer key, WebSocketServer socket) {
        socketPool.put(key, socket);
    }
    public static WebSocketServer getSocket(Integer key) {
        return socketPool.get(key);
    }
    public static void removeSocket(Integer key) {
        socketPool.remove(key);
    }
    
    public static void removeSession(Object key) {
        sessionManager.removeSession(key);
    }
    
    public static void push(String message, Session session) {  // 向客户端推送信息
        synchronized (session) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void tryPush(Object key, String message) {  // 尝试向客户端推送消息
        if (key == null) {
            return;
        }
        Session session = sessionManager.getSession(key);
        if (session != null && session.isOpen()) {
            push(message, session);
        }
    }
    
    public static void removeMatcher(User user) {
        matchPool.remove(user);
    }
    
    public static void addMatcher(User user) {
        matchPool.add(user);
    }
    
    public static int getMatcherNum() {
        return matchPool.size();
    }
    
    public static Iterator<User> getMatchIterator() {
        return matchPool.iterator();
    }
}
