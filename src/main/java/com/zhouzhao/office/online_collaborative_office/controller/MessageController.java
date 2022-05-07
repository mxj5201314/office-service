package com.zhouzhao.office.online_collaborative_office.controller;

import com.zhouzhao.office.online_collaborative_office.common.config.shiro.JwtUtil;
import com.zhouzhao.office.online_collaborative_office.common.constants.JwtConstant;
import com.zhouzhao.office.online_collaborative_office.common.utils.BaseResponse;
import com.zhouzhao.office.online_collaborative_office.dto.RefreshMessageDTO;
import com.zhouzhao.office.online_collaborative_office.service.MessageService;
import com.zhouzhao.office.online_collaborative_office.task.MessageTask;
import com.zhouzhao.office.online_collaborative_office.vo.DeleteMessageRefByIdVO;
import com.zhouzhao.office.online_collaborative_office.vo.MessageByIdVO;
import com.zhouzhao.office.online_collaborative_office.vo.MessageByPageVO;
import com.zhouzhao.office.online_collaborative_office.vo.UpdateUnreadMessageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/message")
@Api("消息模块网络接口")
public class MessageController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageTask messageTask;

    @PostMapping("/getMessageByPage")
    @ApiOperation("获取分页消息列表")
    public BaseResponse searchMessageByPage(@Valid @RequestBody MessageByPageVO vo,
                                            @RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) {
        String userId = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));
        int page = vo.getPage();
        int length = vo.getLength();
        long start = (long) (page - 1) * length;
        List<HashMap> list = messageService.searchMessageByPage(userId, start, length);
        return BaseResponse.success(list);
    }

    @PostMapping("/getMessageById")
    @ApiOperation("根据ID查询消息")
    public BaseResponse getMessageById(@Valid @RequestBody MessageByIdVO vo) {
        HashMap map = messageService.getMessageById(vo.getId());
        return BaseResponse.success(map);
    }

    @PostMapping("/updateUnreadMessage")
    @ApiOperation("未读消息更新成已读消息")
    public BaseResponse updateUnreadMessage(@Valid @RequestBody UpdateUnreadMessageVO vo) {
        long rows = messageService.updateUnreadMessage(vo.getId());
        return BaseResponse.success(rows == 1);
    }

    @PostMapping("/deleteMessageRefById")
    @ApiOperation("删除消息")
    public BaseResponse deleteMessageRefById(@Valid @RequestBody DeleteMessageRefByIdVO vo) {
        long rows = messageService.deleteMessageRefById(vo.getId());
        return BaseResponse.success(rows == 1);
    }

    @GetMapping("/refreshMessage")
    @ApiOperation("刷新用户的消息")
    public BaseResponse refreshMessage(@RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) {
        String userId = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));
        //异步接收消息
        messageTask.receiveAysnc(userId + "");
        //查询接收了多少条消息
        long lastRows = messageService.searchLastCount(userId);
        //查询未读数据
        long unreadRows = messageService.searchUnreadCount(userId);
        RefreshMessageDTO refreshMessageDTO = new RefreshMessageDTO().setLastRows(lastRows).setUnreadRows(unreadRows);
        return BaseResponse.success(refreshMessageDTO);
    }

}

