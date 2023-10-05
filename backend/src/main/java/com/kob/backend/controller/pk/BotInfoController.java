package com.kob.backend.controller.pk;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/pk/")
public class BotInfoController {
    @RequestMapping("/getbotinfo/")
    public List getBotInfo() {
        List<String> list = new LinkedList<>();
        list.add("reddit");
        list.add("apple");
        list.add("tiger");
        return list;
    }
}
