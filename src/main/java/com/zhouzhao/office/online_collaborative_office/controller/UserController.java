package com.zhouzhao.office.online_collaborative_office.controller;


import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.common.config.shiro.JwtUtil;
import com.zhouzhao.office.online_collaborative_office.common.constants.JwtConstant;
import com.zhouzhao.office.online_collaborative_office.common.utils.BaseResponse;
import com.zhouzhao.office.online_collaborative_office.dto.UserSummaryDTO;
import com.zhouzhao.office.online_collaborative_office.entity.TbUser;
import com.zhouzhao.office.online_collaborative_office.service.UserService;
import com.zhouzhao.office.online_collaborative_office.vo.LoginVO;
import com.zhouzhao.office.online_collaborative_office.vo.RegisterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("user")
@Api("用户操做")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("register")
    @ApiOperation("注册")
    public BaseResponse register(@Valid @RequestBody RegisterVO registerVO) throws GlobalException {
        return BaseResponse.success(userService.registerUser(registerVO));
    }

    @PostMapping("login")
    @ApiOperation("登录")
    public BaseResponse login(@Valid @RequestBody LoginVO loginVO) throws GlobalException {
        return BaseResponse.success(userService.login(loginVO));
    }

    @GetMapping("/getUserSummary")
    @ApiOperation("获取用户摘要信息")
    public BaseResponse getUserSummary(@RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) throws GlobalException {
        String userId = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));
        TbUser user = userService.getUserById(userId);
        UserSummaryDTO userSummaryDTO = new UserSummaryDTO();
        BeanUtils.copyProperties(user,userSummaryDTO);
        return BaseResponse.success(userSummaryDTO);
    }


}
