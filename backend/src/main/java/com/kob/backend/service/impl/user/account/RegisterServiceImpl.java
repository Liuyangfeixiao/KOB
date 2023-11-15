package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String, String> map = new HashMap<>();
        if (username == null) {
            map.put("error_message", "用户名不能为空");
            return map;
        }
        if (password == null || confirmedPassword == null) {
            map.put("error_message", "密码不能为空");
            return map;
        }
        username = username.trim(); // 删掉首尾空白字符
        if (username.isEmpty()) {
            map.put("error_message", "用户名不能为空");
            return map;
        }
        if (password.isEmpty() || confirmedPassword.isEmpty()) {
            map.put("error_message", "密码长度不能为0");
            return map;
        }
        if (username.length() > 100) {
            map.put("error_message", "用户名长度不能大于100");
            return map;
        }
        if (password.length() > 100 || confirmedPassword.length() > 100) {
            map.put("error_message", "密码长度不能大于100");
            return map;
        }
        if (!password.equals(confirmedPassword)) {
            map.put("error_message", "两次输入的密码不一致");
            return map;
        }
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username);
        List<User> userList = userMapper.selectList(query);
        if (!userList.isEmpty()) {
            map.put("error_message", "用户名已存在");
            return map;
        }
        String encoded_password = passwordEncoder.encode(password);
        String photo = "https://cdn.acwing.com/media/user/profile/photo/59905_lg_30b7da6829.jpg";
        User user = new User(null, username, encoded_password, photo);
        userMapper.insert(user);
        
        map.put("error_message", "success");
        return map;
    }
}
