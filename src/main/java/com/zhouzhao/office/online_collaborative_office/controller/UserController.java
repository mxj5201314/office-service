package com.zhouzhao.office.online_collaborative_office.controller;


import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.common.config.shiro.JwtUtil;
import com.zhouzhao.office.online_collaborative_office.common.constants.JwtConstant;
import com.zhouzhao.office.online_collaborative_office.common.utils.BaseResponse;
import com.zhouzhao.office.online_collaborative_office.dto.UserSummaryDTO;
import com.zhouzhao.office.online_collaborative_office.entity.TbDept;
import com.zhouzhao.office.online_collaborative_office.entity.TbUser;
import com.zhouzhao.office.online_collaborative_office.service.UserService;
import com.zhouzhao.office.online_collaborative_office.vo.GetMembersVO;
import com.zhouzhao.office.online_collaborative_office.vo.GetUserGroupByDeptVO;
import com.zhouzhao.office.online_collaborative_office.vo.LoginVO;
import com.zhouzhao.office.online_collaborative_office.vo.RegisterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
        Integer userId = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));
        TbUser user = userService.getUserById(userId);
        UserSummaryDTO userSummaryDTO = new UserSummaryDTO();
        BeanUtils.copyProperties(user, userSummaryDTO);
        return BaseResponse.success(userSummaryDTO);
    }


    @PostMapping("/getUserGroupByDept")
    @ApiOperation("查询员工列表，按照部门分组排列")
    @RequiresPermissions(value = {"ROOT", "EMPLOYEE:SELECT"}, logical = Logical.OR)
    public BaseResponse searchUserGroupByDept(@Valid @RequestBody GetUserGroupByDeptVO vo) {
        List<TbDept> tbDepts = userService.getUserGroupByDept(vo.getKeyword());
        return BaseResponse.success(tbDepts);
    }

    @PostMapping("/getMembers")
    @ApiOperation("查询成员")
    @RequiresPermissions(value = {"ROOT", "MEETING:INSERT", "MEETING:UPDATE"}, logical = Logical.OR)
    public BaseResponse getMembers(@Valid @RequestBody GetMembersVO vo) throws GlobalException {

        System.out.println("vo = " + vo.getMembers());
        List<UserSummaryDTO> members = userService.getMembers(vo.getMembers());
        return BaseResponse.success(members);
    }


}
