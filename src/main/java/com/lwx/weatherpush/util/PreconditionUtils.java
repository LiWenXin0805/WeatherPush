package com.lwx.weatherpush.util;

import com.lwx.weatherpush.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * 模仿 guava 的 Preconditions
 * <p>
 * com.google.common.base.Preconditions
 *
 * @author liwx
 */
@Slf4j
public class PreconditionUtils {

    public static void checkState(boolean condition, String msg) throws BusinessException {
        checkState(condition, null, msg);
    }

    public static void checkState(boolean condition, HttpStatus httpStatus, String msg) throws BusinessException {
        if (condition) {
            log.error(msg);
            throw (httpStatus != null)
                    ? new BusinessException(httpStatus, msg)
                    : new BusinessException(msg);
        }
    }

    /**
     * 请求参数有误时，抛异常，并返回 400，表示：
     * - 当前请求无法被服务理解。除非进行修改，否则客户端不应该重复提交这个请求。
     *
     * @param condition 校验条件
     * @param msg       错误提示信息
     * @throws BusinessException 校验条件成立时抛异常
     */
    public static void checkArg(boolean condition, String msg) throws BusinessException {
        checkState(condition, HttpStatus.BAD_REQUEST, msg);
    }

    /**
     * 校验参数为空时，抛异常，并返回 400，表示：请求参数有误。除非进行修改，否则客户端不应该重复提交这个请求。
     *
     * @param o   校验对象
     * @param msg 错误提示信息
     * @throws BusinessException 校验参数为空时抛异常
     */
    public static void checkNotNull(Object o, String msg) throws BusinessException {
        checkState(o == null, HttpStatus.BAD_REQUEST, msg);
    }

    public static void checkNotBlank(String str, String msg) throws BusinessException {
        checkState(StringUtils.isBlank(str), HttpStatus.BAD_REQUEST, msg);
    }

    public static void checkNotEmpty(Collection<?> collection, String msg) throws BusinessException {
        checkState(CollectionUtils.isEmpty(collection), HttpStatus.BAD_REQUEST, msg);
    }

    public static Supplier<BusinessException> newBusinessException(String msg) {
        return () -> new BusinessException(msg);
    }

    public static void throwBusinessException(String msg) throws BusinessException {
        log.error(msg);
        throw new BusinessException(msg);
    }

    private PreconditionUtils() {
    }

}
