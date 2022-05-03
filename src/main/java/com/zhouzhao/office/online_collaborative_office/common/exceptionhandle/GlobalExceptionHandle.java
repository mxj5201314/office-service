package com.zhouzhao.office.online_collaborative_office.common.exceptionhandle;


import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.common.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(GlobalException.class)
    public BaseResponse validationBodyException(GlobalException exception) {
        return BaseResponse.fail(exception.getCode(), exception.getMessage());
    }

}