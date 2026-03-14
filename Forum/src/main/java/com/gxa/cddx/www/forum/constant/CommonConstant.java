package com.gxa.cddx.www.forum.constant;

/**
 * 通用常量
 */
public class CommonConstant {
    
    // 用户角色
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    
    // 状态
    public static final int STATUS_DISABLED = 0;
    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_LOCKED = 2;
    
    // 是否标识
    public static final int NO = 0;
    public static final int YES = 1;
    
    // 帖子状态
    public static final int POST_DELETED = 0;
    public static final int POST_NORMAL = 1;
    public static final int POST_LOCKED = 2;
    
    // 分页默认值
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int MAX_PAGE_SIZE = 100;
    
    // 排序
    public static final String SORT_DESC = "desc";
    public static final String SORT_ASC = "asc";
}

