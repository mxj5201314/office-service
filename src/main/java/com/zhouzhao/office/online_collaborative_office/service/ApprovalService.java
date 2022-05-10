package com.zhouzhao.office.online_collaborative_office.service;

import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;

import java.util.HashMap;
import java.util.List;

public interface ApprovalService {
    List getUserTaskListByPage(HashMap param) throws GlobalException;
    void approveTask(HashMap param) throws GlobalException;
}
