package com.kob.backend.consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private Integer id;
    private Integer sx; // 起点
    private Integer sy;
    private List<Integer> steps; // 每一步的方向
    
    private boolean check_tail_increasing(int step) { // 检查当前回合蛇的长度是否增加
        if (step <= 10) return true;
        return step % 3 == 1;
    }
    public List<Cell> getCells() {
        List<Cell> res = new ArrayList<>();
        int[] dx = {-1, 0, 1, 0};  // 上右下左
        int[] dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        res.add(new Cell(x, y));
        int step = 0; // 回合数
        for (int d : steps) {
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!check_tail_increasing(++step)) {
                // 蛇尾不增加, 删除蛇尾
                res.remove(0);
            }
        }
        return res;
    }
    
    public String getStepsString() {
        StringBuilder sb = new StringBuilder();
        for (int d : steps) {
            sb.append(d);
        }
        return sb.toString();
    }
}
