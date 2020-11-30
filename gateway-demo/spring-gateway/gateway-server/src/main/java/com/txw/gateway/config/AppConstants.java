package com.txw.gateway.config;

public class AppConstants {
    public static final Integer SUCCESS_CODE = 200;
    public static final Integer ERROR_CODE = 401;
    /**
     * POST
     */
    public static final String METHOD_POST = "POST";
    /**
     * GET
     */
    public static final String METHOD_GET = "GET";
    /**
     * token 参数
     */
    public static final String TOKEN = "access-token";
    /**
     * redis token
     */
    public static final String REDIS_KEY_TOKEN = "UMI:TICKET:";
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";
    /**
     * 参数管理 cache name
     */
    public static final String SYS_CONFIG_CACHE = "sys-config";
    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";
    public static final String REPLACE_CHAR = "&";
    public static final String DEFAULT_URL_SPLIT = "/";
    public static final String DEFAULT_INFO_SPLIT = "-";
}