package com.example.web.sendcode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class emailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发送简单文本邮件
     * @param to 接收邮箱
     * @param subject 邮件主题
     * @param text 邮件内容
     */
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("15163322850@163.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        // 这里也可以设置发件人，如果 application.properties 里没配置
        // message.setFrom("your_email@example.com");

        mailSender.send(message);
    }
}