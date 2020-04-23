package com.enong.mall.controller.advice;

import com.enong.mall.common.api.CommonResult;
import com.enong.mall.common.exception.BusinessException;
import com.enong.mall.domain.UmsMember;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 */
@RestControllerAdvice
public class AdviceController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = Exception.class)
    public CommonResult exceptionHandler(Exception exception){
        if(exception instanceof BusinessException){
            return CommonResult.failed("请稍后再试:"+exception.getMessage());
        }else if(exception instanceof MethodArgumentNotValidException){
            return CommonResult.failed("校验错误:"+((MethodArgumentNotValidException) exception).getBindingResult().getFieldError().getDefaultMessage());
        }

        return CommonResult.failed();
    }
}
