package com.kob.backend.consumer;

import lombok.Getter;

public class Game {
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final static int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    @Getter
    private int[][] g;
    
    public Game(Integer rows, Integer cols, Integer inner_walls_count) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
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
}
