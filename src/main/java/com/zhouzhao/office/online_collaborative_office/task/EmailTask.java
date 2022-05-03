package com.zhouzhao.office.online_collaborative_office.task;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope("prototype")
public class EmailTask implements Serializable {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${office.email.system}")
    private String mailbox;

    @Async
    public void sendAsync(SimpleMailMessage message) {
        System.out.println("发送邮件");
        message.setFrom(mailbox);
        javaMailSender.send(message);
    }
}

