package com.zhouzhao.office.online_collaborative_office.common.Exception;


import com.zhouzhao.office.online_collaborative_office.common.enums.RespCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GlobalException extends Exception {


    private String message;

    private Integer code;

    public GlobalException() {
        super(RespCodeEnum.FAIL.getMessage());
        this.message = RespCodeEnum.FAIL.getMessage();
        this.code = RespCodeEnum.FAIL.getCode();
    }

    public GlobalException(RespCodeEnum respCodeEnum) {
        super(respCodeEnum.getMessage());
        this.message = respCodeEnum.getMessage();
        this.code = respCodeEnum.getCode();
    }

    public GlobalException(String message) {
        super(message);
    }

    public GlobalException(String message, Throwable cause) {
        super(message, cause);
    }

    public GlobalException(Integer code, String message) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public GlobalException(Throwable cause) {
        super(cause);
    }

    protected GlobalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
