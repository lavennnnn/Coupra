package cn.hush.Coupra.framework.exception;


import cn.hush.Coupra.framework.errorcode.IErrorCode;
import cn.hush.Coupra.framework.errorcode.BaseErrorCode;

/**
 * @program: Coupra
 * @description: 客户端异常｜用户发起调用请求后因客户端提交参数或其他客户端问题导致的异常
 * @author: Hush
 * @create: 2025-06-27 00:42
 **/

public class ClientException extends AbstractException {

    public ClientException(IErrorCode errorCode) {
        this(null, null, errorCode);
    }

    public ClientException(String message) {
        this(message, null, BaseErrorCode.CLIENT_ERROR);
    }

    public ClientException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ClientException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "ClientException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
