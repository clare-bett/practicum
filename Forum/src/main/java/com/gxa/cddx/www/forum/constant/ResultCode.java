package com.gxa.cddx.www.forum.constant;

/**
 * 统一响应状态码
 */
public class ResultCode {
    
    // 成功
    public static final int SUCCESS = 200;
    
    // 客户端错误
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    
    // 服务器错误
    public static final int INTERNAL_SERVER_ERROR = 500;
    
    // 业务错误码
    public static final int BUSINESS_ERROR = 1001;
    public static final int VALIDATION_ERROR = 1002;
    public static final int USER_NOT_FOUND = 1003;
    public static final int USER_ALREADY_EXISTS = 1004;
    public static final int WRONG_PASSWORD = 1005;
    public static final int POST_NOT_FOUND = 1006;
    public static final int CATEGORY_NOT_FOUND = 1007;
    public static final int REPLY_NOT_FOUND = 1008;
    public static final int PERMISSION_DENIED = 1009;
}

