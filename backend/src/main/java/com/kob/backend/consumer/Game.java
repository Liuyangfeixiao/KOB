package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.WebSocketUtils;
import com.kob.backend.pojo.Record;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final static int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    @Getter
    private final Player playerA, playerB;
    @Getter
    private int[][] g;
    
    private Integer nextStepA = null;// null 表示没有获取到下一步玩家的操作
    private Integer nextStepB = null;
    private ReentrantLock lock = new ReentrantLock();
    private String status = "playing"; // playing -> finished
    private String loser = ""; // all: 平局, A: A输, B: B输
    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try { // 当报异常的时候也会自动解锁，不会产生死锁
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }
    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }
    
    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Integer idB) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        playerA = new Player(idA, rows-2, 1, new ArrayList<>());
        playerB = new Player(idB, 1, cols-2, new ArrayList<>());
    }
    
    private boolean draw() { // 画地图
        // 先清空
        for (int i = 0; i < this.rows; ++i) {
            for (int j = 0; j < this.cols; ++j) {
                this.g[i][j] = 0;  // 0 代表空地
            }
        }
        // 给四周加墙
        for (int i = 0; i < this.rows; ++i) {
            this.g[i][0] = 1;
            this.g[i][this.cols - 1] = 1;
        }
        for (int i = 0; i < this.cols; ++i) {
            this.g[0][i] = 1;
            this.g[this.rows - 1][i] = 1;
        }
        
        // 创建随机障碍物
        for (int i = 0; i < this.inner_walls_count / 2; ++i) {
            for (int j = 0; j < 1000; ++j) {
                int x = (int) (Math.random() * this.rows);  // [0, rows-1]
                int y = (int) (Math.random() * this.cols);
                if (g[x][y] == 1 || g[this.rows - 1 - x][this.cols - 1 - y] == 1) continue;
                // 左下角与右上角不能有障碍物
                if (x == this.rows - 2 && y == 1 || y == this.cols-2 && x == 1) continue;
                // 障碍物关于中心对称
                g[x][y] = g[this.rows - 1 - x][this.cols - 1 - y] = 1;
                break;
            }
        }
        // 判断左下角与右上角是否联通
        return checkConnectivity(this.rows - 2, 1, 1, this.cols - 2);
    }
    private boolean checkConnectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1; // 标记
        for (int i = 0; i < 4; ++i) {
            int x = sx + directions[i][0];
            int y = sy + directions[i][1];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0 && checkConnectivity(x, y, tx, ty)) {
                g[sx][sy] = 0;
                return true;
            }
        }
        g[sx][sy] = 0;
        return false;
    }
    public void createMap() {
        for (int i = 0; i < 1000; ++i) {
            if (draw()) {
                break;
            }
        }
    }
    
    private boolean nextStep() {  // 等待两名玩家的下一步操作
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < 50; ++i) { // 最多等待5s
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {  // 都读到了两名玩家的下一步操作
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    private void sendAllMessage(String message) {
        WebSocketUtils.tryPush(playerA.getId(), message);
        WebSocketUtils.tryPush(playerB.getId(), message);
    }
    // 向Client发送游戏结果
    private void sendResult() {
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();  // 发送给前端之前先存储到数据库中
        sendAllMessage(resp.toJSONString());
    }
    private void sendMove() { // 向两个Client发送移动信息
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            // 返回之后需要清空操作
            nextStepA = null;
            nextStepB = null;
        } finally {
            lock.unlock();
        }
        
    }
    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cellA = cellsA.get(n-1);  // A 的 蛇头
        if (g[cellA.x][cellA.y] == 1) return false;
        // 判断蛇的头部是否和身子在一块
        for (int i = 0; i < n - 1; ++i) {
            if (Objects.equals(cellsA.get(i).x, cellA.x) && Objects.equals(cellsA.get(i).y, cellA.y)) {
                return false;
            }
        }
        // 判断是否和B有重合， 由于地图是13*14，两条蛇的奇偶性不同
        for (int i = 0; i < n - 1; ++i) {
            if (Objects.equals(cellsB.get(i).x, cellA.x) && Objects.equals(cellsB.get(i).y, cellA.y)) {
                return false;
            }
        }
        return true;
    }
    private void judge() {  // 判断两名玩家的操作是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();
        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if (!validA || !validB) {
            this.status = "finished";
            if (!validA && !validB) {
                this.loser = "all";
            } else if (!validA) {
                this.loser = "A";
            } else {
                this.loser = "B";
            }
        }
    }
    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < this.rows; ++i) {
            for (int j = 0; j < this.cols; ++j) {
                res.append(this.g[i][j]);
            }
        }
        return res.toString();
    }
    private void saveToDatabase() {
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                this.loser,
                new Date()
        );
        WebSocketServer.recordMapper.insert(record);
    }
    @Override
    public void run() { // 新线程的入口函数
        for (int i = 0; i < 1000; ++i) {
            if (nextStep()) {  // 等待两名玩家的下一步操作
                sendMove();  // 无论游戏是否结束都要
                judge();
                if (status.equals("finished")) { // 游戏结束，将结果广播给玩家
                    sendResult();
                    break;
                }
            } else {  // 如果有玩家下一步操作没有输入，结束游戏
                status = "finished";
                // 涉及到多线程，所以要加锁
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null) { // 判定输赢
                        loser = "all";
                    } else if (nextStepA == null) {
                        loser = "A";
                    } else {
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
        
    }
}
