package com.zhouzhao.office.online_collaborative_office.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.common.config.shiro.JwtUtil;
import com.zhouzhao.office.online_collaborative_office.common.enums.RespCodeEnum;
import com.zhouzhao.office.online_collaborative_office.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    @Value("${workflow.url}")
    private String workflow;

    @Value("${office.code}")
    private String code;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public List getUserTaskListByPage(HashMap param) throws GlobalException {
        param.put("code", code);
        String url = workflow + "/workflow/searchUserTaskListByPage";
        HttpResponse resp = HttpRequest.post(url).header("Content-Type", "application/json")
                .header("token", ((String) param.get("token")))
                .body(JSONUtil.toJsonStr(param)).execute();
        if (resp.getStatus() == 200) {
            JSONObject json = JSONUtil.parseObj(resp.body());
            ArrayList result = json.get("result", ArrayList.class);
            return result;
        } else {
            throw new GlobalException(RespCodeEnum.FAIL.getCode(), "查询工作流审批任务失败");
        }
    }

    @Override
    public void approveTask(HashMap param) throws GlobalException {
        param.put("code", code);
        String url = workflow + "/workflow/approvalTask";
        HttpResponse resp = HttpRequest.post(url).header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(param)).execute();
        if (resp.getStatus() != 200) {
            throw new GlobalException(RespCodeEnum.FAIL.getCode(), "查询工作流审批任务失败");
        }
    }
}