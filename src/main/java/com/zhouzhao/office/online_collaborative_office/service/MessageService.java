package com.zhouzhao.office.online_collaborative_office.service;


import com.zhouzhao.office.online_collaborative_office.entity.MessageEntity;
import com.zhouzhao.office.online_collaborative_office.entity.MessageRefEntity;

import java.util.HashMap;
import java.util.List;

public interface MessageService {
    String insertMessage(MessageEntity entity);
    List<HashMap> searchMessageByPage(String userId, long start, int length);
    HashMap getMessageById(String id);
    String insertRef(MessageRefEntity entity);
    long searchUnreadCount(String userId);
    long searchLastCount(String userId);
    long updateUnreadMessage(String id);
    long deleteMessageRefById(String id);
    long deleteUserMessageRef(int userId);
}
