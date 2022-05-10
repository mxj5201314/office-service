package com.zhouzhao.office.online_collaborative_office.controller;

import cn.hutool.json.JSONUtil;
import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.common.config.shiro.JwtUtil;
import com.zhouzhao.office.online_collaborative_office.common.constants.JwtConstant;
import com.zhouzhao.office.online_collaborative_office.common.utils.BaseResponse;
import com.zhouzhao.office.online_collaborative_office.service.ApprovalService;
import com.zhouzhao.office.online_collaborative_office.vo.ApproveTaskVO;
import com.zhouzhao.office.online_collaborative_office.vo.GetUserTaskListByPageVO;
import com.zhouzhao.office.online_collaborative_office.vo.RecieveNotifyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/approval")
@Api("审批模块Web接口")
@Slf4j
public class ApprovalController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ApprovalService approvalService;

    @PostMapping("/getUserTaskListByPage")
    @ApiOperation("查询审批任务分页列表")
    public BaseResponse searchUserTaskListByPage(@Valid @RequestBody GetUserTaskListByPageVO vo, @RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) throws GlobalException {
        int userId = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));
        HashMap param = JSONUtil.parse(vo).toBean(HashMap.class);
        param.put("token", token.substring(JwtConstant.TOKEN_HEAD.length()));
        param.put("userId", userId);
        List list = approvalService.getUserTaskListByPage(param);
        return BaseResponse.success(list);
    }

    @PostMapping("/approveTask")
    @ApiOperation("审批任务")
    @RequiresPermissions(value = {"ROOT", "WORKFLOW:APPROVAL"}, logical = Logical.OR)
    public BaseResponse approveTask(@Valid @RequestBody ApproveTaskVO vo) throws GlobalException {
        HashMap param = JSONUtil.parse(vo).toBean(HashMap.class);
        approvalService.approveTask(param);
        return BaseResponse.success();
    }

    @PostMapping("/recieveNotify")
    @ApiOperation("接受工作流通知")
    public BaseResponse recieveNotify(@Valid @RequestBody RecieveNotifyVO vo) {
        if (vo.getResult().equals("同意")) {
            log.debug(vo.getUuid() + "的会议审批通过");
        } else {
            log.debug(vo.getUuid() + "的会议审批不通过");
        }
        return BaseResponse.success();
    }
}