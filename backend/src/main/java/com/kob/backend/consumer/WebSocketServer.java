package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.consumer.utils.WebSocketUtils;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;


@Component
@ServerEndpoint(value = "/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {  // 每一个连接通过这个类的实例来维护
    // 用来存储每个客户端对应的WebSocketServer对象，线程安全
    private User user;
    // 注入一个UserMapper，用来查询用户信息，Spring中注入的是单例的
    // 由于WebSocketServer是多对象的，所以不能直接注入，需要通过一个set方法注入
    private static UserMapper userMapper;
    private Game game = null;
    @Autowired
    private void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;  // 静态变量访问的时候需要用内存来访问
    }
    
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        // 先鉴权，如果鉴权通过则存储WebsocketSession，否则关闭连接
        // 鉴权通过后，将用户信息存储在WebSocketServer中
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        if (this.user == null) {  // 用户不存在则关闭连接
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        WebSocketUtils.addSession(this.user.getId(), session);
        System.out.println("WebSocket Connected! ID: " + session.getId());
    }
    @OnClose
    public void OnClose(Session session) {
        // 关闭连接时删除WebsocketSession
        
        if (this.user != null) {
            WebSocketUtils.removeSession(this.user.getId());  // 删除session
            WebSocketUtils.removeMatcher(this.user);  // 删除匹配池用户
        }
        System.out.println("WebSocket Disconnected! ID: " + session.getId());
        
    }
    private void startMatching() {
        System.out.println("start matching");
        WebSocketUtils.addMatcher(this.user);
        while (WebSocketUtils.getMatcherNum() >= 2) {
            Iterator<User> it = WebSocketUtils.getMatchIterator();
            User a =  it.next(), b = it.next();
            WebSocketUtils.removeMatcher(a);
            WebSocketUtils.removeMatcher(b);
            
            // 我们要将GameMap存到A和B的连接中，目前先用局部变量保存
            // 生成地图
            Game game = new Game(13, 14, 20);
            game.createMap();
            // 向A发送B的具体信息
            JSONObject respA = new JSONObject();
            respA.put("event", "start-matching");
            respA.put("opponent_username", b.getUsername());
            respA.put("opponent_photo", b.getPhoto());
            respA.put("gamemap", game.getG());
            // 获取A的连接, 向前端发消息
            WebSocketUtils.tryPush(a.getId(), respA.toJSONString());
            
            // 向B发送A的具体信息
            JSONObject respB = new JSONObject();
            respB.put("event", "start-matching");
            respB.put("opponent_username", a.getUsername());
            respB.put("opponent_photo", a.getPhoto());
            respB.put("gamemap", game.getG());
            // 获取B的连接信息
            WebSocketUtils.tryPush(b.getId(), respB.toJSONString());
        }
        
    }
    private void stopMatching() {
        System.out.println("stop matching");
        WebSocketUtils.removeMatcher(this.user);
    }
    @OnMessage
    public void OnMessage(String message, Session session) {  // 一般当作一个路由
        // 收到消息时处理消息
        System.out.println("WebSocket收到消息. ID: " + session.getId() + " Message: " + message);
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            startMatching();
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        }
    }
    
    @OnError
    public void OnError(Session session, Throwable error) {
        // 发生错误时处理错误
        error.printStackTrace();
    }
}
