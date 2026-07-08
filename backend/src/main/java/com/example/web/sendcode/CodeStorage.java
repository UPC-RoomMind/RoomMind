package com.example.web.sendcode;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CodeStorage {

    private final Map<String, String> codeMap = new ConcurrentHashMap<>();

    // 保存验证码
    public void saveCode(String email, String code) {
        codeMap.put(email, code);
        // 这里没有实现过期，需要自己加，比如用 ScheduledExecutorService
    }
    // 获取验证码
    public String getCode(String email) {
        return codeMap.get(email);
    }
}
