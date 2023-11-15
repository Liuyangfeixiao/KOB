package com.kob.backend.controller.user.account;

import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RegisterController {
    @Autowired
    private RegisterService registerService;
    
    @PostMapping("/user/account/register/")
    public Map<String, String> register(@RequestBody Map<String, Object> map) { // 需要传入一个JSON格式的
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        String confirmedPassword = (String) map.get("confirmedPassword");
        return registerService.register(username, password, confirmedPassword);
    }
}
