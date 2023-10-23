import { AcGameObject } from "./AcGameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall";
export class GameMap extends AcGameObject {
    constructor(ctx, parent) {
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0; // 一个单位的长度
        this.rows = 13;
        this.cols = 14;
        
        this.inner_walls_count = 20;
        this.walls = [];

        this.snakes = [
            new Snake({id: 0, color: "#fc6b03", r: this.rows - 2, c: 1}, this),
            new Snake({id: 1, color: "#98fc03", r: 1, c: this.cols -2}, this),
        ];

    }

    check_connectivity(g, sx, sy, tx, ty) {
        if (sx == tx && sy == ty) return true;

        // 当前位置标记为已经走过
        g[sx][sy] = true;
        // 定义偏移量
        let dx = [-1, 0, 1, 0], dy = [0, 1, 0, -1];
        // 判断是否联通
        for (let i = 0; i < 4; i ++ ) {
            let x  = sx + dx[i], y = sy + dy[i];
            if (!g[x][y] && this.check_connectivity(g, x, y, tx, ty))
                return true;
        }
        return false;
    }

    create_walls() {
        const g = []; // 有墙为 true， 没墙为 false
        for (let r = 0; r < this.rows; r ++ ) {
            g[r] = [];
            for (let c = 0; c < this.cols; c ++) {
                g[r][c] = false;
            }
        }
        // 给四周加上障碍物
        for (let c = 0; c < this.cols; c ++) {
            g[0][c] = g[this.rows-1][c] = true;
        }

        for (let r = 0; r < this.rows; r ++ ) {
            g[r][0] = g[r][this.cols-1] = true;
        }

        // 创建随机障碍物
        for (let i = 0; i < this.inner_walls_count / 2; i ++) {
            for (let j = 0; j < 1000; j ++) {
                let r = parseInt(Math.random() * this.rows);
                let c = parseInt(Math.random() * this.cols);
                if (g[r][c] || g[this.rows - 1 - r][this.cols - 1 - c]) continue;
                // 左下角与右上角不能覆盖
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols-2) continue;
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = true;
                break;
            }
        }
        
        const copy_g = JSON.parse(JSON.stringify(g));
        if (!this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2)) {
            return false;
        }

        // 画出障碍物
        for (let r = 0; r < this.rows; r ++ ) {
            for (let c = 0; c < this.cols; c ++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
        return true;
    }

    add_listening_events() {
        // canvas 聚焦
        this.ctx.canvas.focus();
        const [snake0, snake1] = this.snakes;
        this.ctx.canvas.addEventListener("keydown", e => {
            if (e.key === 'w') snake0.set_direction(0); // 将方向改为上
            else if (e.key === 'd') snake0.set_direction(1);  // 方向改为右
            else if (e.key === 's') snake0.set_direction(2); // 方向改为下
            else if (e.key === 'a') snake0.set_direction(3); // 方向改为左
            else if (e.key === 'ArrowUp') snake1.set_direction(0); // 第二条蛇改变方向
            else if (e.key === 'ArrowRight') snake1.set_direction(1);
            else if (e.key === 'ArrowDown') snake1.set_direction(2);
            else if (e.key === 'ArrowLeft') snake1.set_direction(3);
        })
    }

    start() {
        // 开始时执行一次
        for (let i = 0; i < 1000;i ++) {
            if (this.create_walls())
                break;
        }

        this.add_listening_events();
        
    }

    update_size() {
        // 每一帧都更新边长, 将浮点数转化为整数
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    check_ready() {  // 判断两条蛇是否都准备好了下一回合
        for (const snake of this.snakes) {
            if (snake.status !== "idle") return false;
            if (snake.direction === -1) return false;
        }

        return true;
    }

    next_step() { // 让两条蛇进入下一回合
        for (const snake of this.snakes) {
            snake.next_step();
        }
    }

    check_valid(cell) {  // 检测目标位置是否合法
        // 是否撞到两条蛇的身体和墙壁
        for (const wall of this.walls) {
            if (wall.r === cell.r && wall.c === cell.c)
                return false;
        }

        for (const snake of this.snakes) {
            let k = snake.cells.length;
            if (!snake.check_tail_increasing()) {
                // 当蛇尾会前进时，不判断蛇尾
                k --;
            }
            for (let i = 0; i < k; i ++) {
                if (snake.cells[i].r === cell.r && snake.cells[i].c === cell.c) {
                    return false;
                }
            }
        }
        return true;
    }

    update() {
        // 每一帧执行一次
        this.update_size();
        if (this.check_ready()) {
            this.next_step();
        }
        this.render();
    }
    render() {
        const color_even = "#90b28d", color_odd = "#ecf4eb";
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if ((c + r) % 2 == 0) {
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }
                // canvas 坐标 先列后行
                // (x, y, width, height)
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}