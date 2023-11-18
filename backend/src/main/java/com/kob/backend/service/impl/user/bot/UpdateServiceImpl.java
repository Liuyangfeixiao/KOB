package com.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Service
public class UpdateServiceImpl implements UpdateService {
    @Autowired
    private BotMapper botMapper;
    @Override
    public Map<String, String> update(Map<String, String> data) {
        // 提取当前用户
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        
        int bot_id = Integer.parseInt(data.get("bot_id"));
        String title = data.get("title");
        String content = data.get("content");
        String description = data.get("description");
        
        Bot bot = botMapper.selectById(bot_id);
        Map<String, String> map = new HashMap<>();
        if (bot == null) {
            map.put("error_message", "该Bot不存在或已经被删除");
            return map;
        }
        if (!bot.getUserId().equals(user.getId())) {
            map.put("error_message", "该Bot不属于当前用户，无法修改");
            return map;
        }
        if (title == null || title.isEmpty()) {
            map.put("error_message", "标题不能为空");
            return map;
        }
        if (title.length() > 100) {
            map.put("error_message", "标题长度不能超过100");
            return map;
        }
        if (description == null || description.isEmpty()) {
            description = "这个用户很懒，什么都没留下~";
        }
        if (description.length() > 300) {
            map.put("error_message", "Bot描述长度不能超过300");
            return map;
        }
        if (content == null || content.isEmpty()) {
            map.put("error_message", "代码不能为空");
            return map;
        }
        if (content.length() > 10000) {
            map.put("error_message", "代码长度不能超过10000");
            return map;
        }
        // 更新bot
        bot.setTitle(title);
        bot.setContent(content);
        bot.setDescription(description);
        bot.setModifytime(new Date());
        botMapper.updateById(bot);
        map.put("error_message", "success");
        return map;
    }
}
