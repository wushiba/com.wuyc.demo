package com.yfshop.common.exception;

import com.yfshop.common.api.ErrorCode;
import com.yfshop.common.api.IErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * 断言处理类，用于抛出各种API异常
 */
public class Asserts {
    private static void fail(String message) {
        throw new ApiException(message);
    }

    private static void fail(IErrorCode errorCode) {
        throw new ApiException(errorCode);
    }

    private static void fail(int code, String message) {
        fail(new ErrorCode(code, message));
    }


    /**
     * 断言为true
     *
     * @param expression 表达式
     */
    public static void assertTrue(boolean expression, IErrorCode errorCode) {
        if (!expression) {
            fail(errorCode);
        }
    }


    /**
     * 断言为true
     *
     * @param expression 表达式
     * @param code       异常错误码
     * @param message    异常信息
     */
    public static void assertTrue(boolean expression, Integer code, String message) {
        if (!expression) {
            fail(code, message);
        }
    }

    /**
     * 断言为false
     *
     * @param expression 表达式
     */
    public static void assertFalse(boolean expression, IErrorCode errorCode) {
        if (expression) {
            fail(errorCode);
        }
    }

    /**
     * 断言为false
     *
     * @param expression 表达式
     * @param code       异常错误码
     * @param message    异常信息
     */
    public static void assertFalse(boolean expression, Integer code, String message) {
        if (expression) {
            fail(code, message);
        }
    }


    /**
     * 断言为空
     *
     * @param target 目标对象
     */
    public static void assertNull(Object target, IErrorCode errorCode) {
        if (target != null) {
            fail(errorCode);
        }
    }

    /**
     * 断言为空
     *
     * @param target  目标对象
     * @param code    异常错误码
     * @param message 异常信息
     */
    public static void assertNull(Object target, Integer code, String message) {
        if (target != null) {
            fail(code, message);
        }
    }


    /**
     * 断言非空
     *
     * @param target 目标对象
     */
    public static void assertNonNull(Object target, IErrorCode errorCode) {
        if (target == null) {
            fail(errorCode);
        }
    }

    /**
     * 断言非空
     *
     * @param target  目标对象
     * @param code    异常错误码
     * @param message 异常信息
     */
    public static void assertNonNull(Object target, Integer code, String message) {
        if (target == null) {
            fail(code, message);
        }
    }


    /**
     * 断言字符串非空(null,""," ")
     *
     * @param sequence the string sequence
     */
    public static void assertStringNotBlank(CharSequence sequence, IErrorCode errorCode) {
        if (StringUtils.isBlank(sequence)) {
            fail(errorCode);
        }
    }

    /**
     * 断言字符串非空(null,""," ")
     *
     * @param sequence the string sequence
     * @param code     异常错误码
     * @param message  异常信息
     */
    public static void assertStringNotBlank(CharSequence sequence, Integer code, String message) {
        if (StringUtils.isBlank(sequence)) {
            fail(code, message);
        }
    }


    /**
     * 断言字符串为空(null,""," ")
     *
     * @param sequence the string sequence
     */
    public static void assertStringBlank(CharSequence sequence, IErrorCode errorCode) {
        if (StringUtils.isNotBlank(sequence)) {
            fail(errorCode);
        }
    }

    /**
     * 断言字符串为空(null,""," ")
     *
     * @param sequence the string sequence
     * @param code     异常错误码
     * @param message  异常信息
     */
    public static void assertStringBlank(CharSequence sequence, Integer code, String message) {
        if (StringUtils.isNotBlank(sequence)) {
            fail(code, message);
        }
    }

    /**
     * 断言两对象相等
     *
     * @param a the object a
     * @param b the object b
     */
    public static void assertEquals(Object a, Object b, IErrorCode errorCode) {
        if (!Objects.equals(a, b)) {
            fail(errorCode);
        }
    }

    /**
     * 断言两对象相等
     *
     * @param a       the object a
     * @param b       the object b
     * @param code    异常错误码
     * @param message 异常信息
     */
    public static void assertEquals(Object a, Object b, Integer code, String message) {
        if (!Objects.equals(a, b)) {
            fail(code, message);
        }
    }

    /**
     * 断言两对象不相等
     *
     * @param a the object a
     * @param b the object b
     */
    public static void assertNotEquals(Object a, Object b, IErrorCode errorCode) {
        if (Objects.equals(a, b)) {
            fail(errorCode);
        }
    }

    /**
     * 断言两对象不相等
     *
     * @param a       the object a
     * @param b       the object b
     * @param code    异常错误码
     * @param message 异常信息
     */
    public static void assertNotEquals(Object a, Object b, Integer code, String message) {
        if (Objects.equals(a, b)) {
            fail(code, message);
        }
    }

    /**
     * 断言集合不能为空
     *
     * @param collection 集合
     */
    public static void assertCollectionNotEmpty(Collection collection, IErrorCode errorCode) {
        if (CollectionUtils.isEmpty(collection)) {
            fail(errorCode);
        }
    }

    /**
     * 断言集合不能为空
     *
     * @param collection 集合
     * @param code       异常错误码
     * @param message    异常信息
     */
    public static void assertCollectionNotEmpty(Collection collection, Integer code, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            fail(code, message);
        }
    }

    /**
     * 断言集合为空
     *
     * @param collection 集合
     */
    public static void assertCollectionEmpty(Collection collection, IErrorCode errorCode) {
        if (!CollectionUtils.isEmpty(collection)) {
            fail(errorCode);
        }
    }

    /**
     * 断言集合为空
     *
     * @param collection 集合
     * @param code       异常错误码
     * @param message    异常信息
     */
    public static void assertCollectionEmpty(Collection collection, Integer code, String message) {
        if (!CollectionUtils.isEmpty(collection)) {
            fail(code, message);
        }
    }
}