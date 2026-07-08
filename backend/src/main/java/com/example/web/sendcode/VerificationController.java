package com.example.web.sendcode;

import com.example.web.service.impl.VaildCodelmpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/auth")
public class VerificationController {
    @Autowired private VaildCodelmpl vad;

    @Autowired
    private CodeStorage codeStorage;

    @Autowired
    private emailService emailService;

    @PostMapping("/sendCode")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "邮箱不能为空"));
        }

        // 生成6位验证码
        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        // 保存到内存map
        vad.saveCode(email, code);
        codeStorage.saveCode(email, code);
        // 发送邮件
        try {
            emailService.sendSimpleMessage(email, "注册验证码", "您的验证码是：" + code);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "发送邮件失败"));
        }
        return ResponseEntity.ok(Map.of("success", true, "message", "验证码已发送"));
    }
}


