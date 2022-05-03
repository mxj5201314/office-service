package com.zhouzhao.office.online_collaborative_office.common.utils;

import com.zhouzhao.office.online_collaborative_office.common.enums.RespCodeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseResponse<T> {

    //状态码
    private Integer code;

    private String msg;

    private T data;

    private String token;


    private BaseResponse() {

    }

    private BaseResponse(Integer code, String message) {
        this.code = code;
        this.msg = message;
    }

    private BaseResponse(RespCodeEnum respCodeEnum) {
        this.code = respCodeEnum.getCode();
        this.msg = respCodeEnum.getMessage();
    }

    private BaseResponse(RespCodeEnum respCodeEnum, T data) {
        this.code = respCodeEnum.getCode();
        this.msg = respCodeEnum.getMessage();
        this.data = data;
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(RespCodeEnum.SUCCESS, data);
    }

    public static <T> BaseResponse<T> success() {
        return new BaseResponse<T>(RespCodeEnum.SUCCESS, null);
    }

    public static <T> BaseResponse<T> fail(RespCodeEnum respCodeEnum) {
        return new BaseResponse<T>(respCodeEnum);
    }

    public static <T> BaseResponse<T> fail(String message) {
        return new BaseResponse<T>(RespCodeEnum.FAIL.getCode(), message);
    }

    public static <T> BaseResponse<T> fail(Integer code, String message) {
        return new BaseResponse<T>(code, message);
    }


}
