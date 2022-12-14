package com.lwx.weatherpush.aop;

import com.lwx.weatherpush.exception.BusinessException;
import com.lwx.weatherpush.standard.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@ResponseBody
@SuppressWarnings("all")
public class GlobalExceptionHandler {
    private static final String DEFAULT_MESSAGE = "未确定的异常信息";

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    private static String getMessage(Exception e) {
        String message = e.getMessage();
        if (!StringUtils.hasLength(message)) {
            message = DEFAULT_MESSAGE;
        }
        return message;
    }

    private static StackTraceElement getRootErrorInfo(Exception e) {
        StackTraceElement data = null;
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace != null && stackTrace.length != 0) {
            data = stackTrace[0];
        }
        return data;
    }

    public static RestResponse handleGlobalException(Exception exception) {
        int code = exception instanceof BindException ? 400 : 500;
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setCode(code);
        restResponse.setMessage(getMessage(exception));
        restResponse.setSuccess(false);
        restResponse.setData(getRootErrorInfo(exception));
        return restResponse;
    }

    @ExceptionHandler({BusinessException.class, NullPointerException.class})
    public RestResponse<StackTraceElement> handleException(BusinessException e) {
        log.error("An business exception occurred.", e);

        RestResponse<StackTraceElement> restResponse = new RestResponse<>();
        int code = e.getCode() == null ? HttpStatus.INTERNAL_SERVER_ERROR.value() : e.getCode();
        restResponse.setCode(code);
        restResponse.setSuccess(false);

        // 获取异常堆栈的第一个元素
        StackTraceElement data = getRootErrorInfo(e);
        restResponse.setData(data);

        restResponse.setMessage(getMessage(e));
        return restResponse;
    }

    @ExceptionHandler(Exception.class)
    public RestResponse<Object> handleException(Exception e) {
        log.error("An unexpected exception occurred.", e);
        // 获取异常堆栈的第一个元素
        Map<String, Object> data = new HashMap<>(32);
        data.put("cause", e.getCause() == null ? null : e.getCause().getMessage());
        data.put("rootError", getRootErrorInfo(e));
        data.put("message", e.getMessage());
        return RestResponse.error(data, DEFAULT_MESSAGE);
    }

    @ExceptionHandler(BindException.class)
    public RestResponse<List<ObjectError>> handleException(BindException e) {
        log.error("An illegal argument exception occurred.", e);
        List<ObjectError> allErrors = e.getAllErrors();
        String message = allErrors.parallelStream().map(objectError -> {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                String field = fieldError.getField();
                String defaultMessage = fieldError.getDefaultMessage();
                return field + defaultMessage;
            }
            return objectError.toString();
        }).collect(Collectors.joining(";"));

        RestResponse<List<ObjectError>> response = new RestResponse<>();
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setData(allErrors);
        response.setMessage(message);
        response.setSuccess(false);

        return response;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public RestResponse<StackTraceElement> handleException(MaxUploadSizeExceededException e) {
        log.error("a file upload exception occurred.", e);

        RestResponse<StackTraceElement> restResponse = new RestResponse<>();
        restResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        restResponse.setSuccess(false);

        // 获取异常堆栈的第一个元素
        StackTraceElement data = getRootErrorInfo(e);
        restResponse.setData(data);
        restResponse.setMessage("上传的文件不能超过" + maxFileSize);
        return restResponse;
    }

}
