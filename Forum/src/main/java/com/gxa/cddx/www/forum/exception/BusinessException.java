package com.gxa.cddx.www.forum.exception;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {
    
    private Integer code;
    
    public BusinessException(String message) {
        super(message);
        this.code = 1001;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 1001;
    }
    
    public Integer getCode() {
        return code;
    }
}

