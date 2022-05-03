package com.zhouzhao.office.online_collaborative_office.common.exceptionhandle;

import com.zhouzhao.office.online_collaborative_office.common.enums.RespCodeEnum;
import com.zhouzhao.office.online_collaborative_office.common.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class RequestValidateExceptionHandle {

    /**
     * 校验错误拦截处理
     *
     * @param exception 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse validationBodyException(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        String message = "";
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            errors.forEach(p -> {
                FieldError fieldError = (FieldError) p;
                log.error("数据检查失败 : 项目{" + fieldError.getObjectName() + "},字段{" + fieldError.getField() +
                        "},错误信息{" + fieldError.getDefaultMessage() + "}");

            });
            if (errors.size() > 0) {
                FieldError fieldError = (FieldError) errors.get(0);
                message = fieldError.getDefaultMessage();
            }
        }

        return BaseResponse.fail(RespCodeEnum.ERR_METHOD_ARGUMENT.getCode(),
                message != null ? message : RespCodeEnum.ERR_METHOD_ARGUMENT.getMessage());
    }

    /**
     * 参数类型转换错误
     *
     * @param exception 错误
     * @return 错误信息
     */
    //@ExceptionHandler(HttpMessageConversionException.class)
    //public Message parameterTypeException(HttpMessageConversionException exception) {
    //    log.error(exception.getCause().getLocalizedMessage());
    //    return Message.fail("类型转换错误");
    //
    //}
}