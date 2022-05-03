package com.zhouzhao.office.online_collaborative_office.common.enums;

import lombok.Getter;
import lombok.ToString;

@ToString
public enum RespCodeEnum {
    SUCCESS(2000, "操作成功"),
    FAIL(4000, "操作失败"),
    ERR_METHOD_ARGUMENT(4001, "参数异常"),
    ERR_ADMINISTRATOR_EXIST(4002, "管理员已存在"),
    ERR_REDIS_SET(4003, "Redis存储失败"),
    ERR_REDIS_UPDATE(4004, "Redis更新失败"),
    ERR_REDIS_REMOVE(4005, "Redis删除失败"),
    ERR_NOT_LOGIN(4006, "请先登录"),
    ERR_NOT_GRT_OPENID(4007, "openid为空"),
    ERR_USER_EXIST(4008, "用户不存在"),
    ERR_CHECKIN_PHOTO(4009, "签到图片为空"),
    ERR_TOKEN_INVALID(4010, "令牌无效"),
    ERR_TOKEN_EXPIRE(4011, "令牌已过期"),
    ERR_CHECKIN_FACE_RECOGNITION(4012, "人脸识别失败"),
    ERR_CHECKIN_FACE_REGISTER(4013, "人脸入库失败"),
    ERR_CHECKIN_EPIDEMIC_LEVEL(4014, "疫情风险等级查询失败"),
    ERR_CHECKIN_TIMEOUT(4015, "超过下班时间，无法打卡"),
    ERR_CHECKIN_FACE_NOT_EXISTS(4014, "人脸数据不存在"),
    ERR_CHECKIN(4015, "签到失败"),
    ERR_CHECKIN_EXISTS(4016, "请勿重复签到")
    ;


    @Getter
    private final Integer code;
    @Getter
    private final String message;

    RespCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
