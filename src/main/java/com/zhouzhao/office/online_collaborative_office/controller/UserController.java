package com.zhouzhao.office.online_collaborative_office.controller;


import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.common.utils.BaseResponse;
import com.zhouzhao.office.online_collaborative_office.service.UserService;
import com.zhouzhao.office.online_collaborative_office.vo.LoginVO;
import com.zhouzhao.office.online_collaborative_office.vo.RegisterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("user")
@Api("用户操做")
@Slf4j
public class UserController {
    @Autowired
    UserService userService;


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

}
