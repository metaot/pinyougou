package cn.itcast.core.controller.login;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/name.do")
    public Map<String, String> showName(){
        Map<String, String> map = new HashMap<>();
        // 从springsecurity容器中取出用户信息
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        // SecurityContextHolder.getContext().getAuthentication().
        map.put("loginName", loginName);
        return map;
    }
}
