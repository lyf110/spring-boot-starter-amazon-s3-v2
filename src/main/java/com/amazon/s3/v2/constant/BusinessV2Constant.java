package com.amazon.s3.v2.constant;

import java.time.format.DateTimeFormatter;

/**
 * @author liuyangfang
 * @description 常量类
 * @since 2023/5/31 14:23:42
 */
public class BusinessV2Constant {
    public static final DateTimeFormatter FILE_NAME_PATTERN = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    public static final String FILE_SEPARATOR = "/";
    public static final String FILENAME_LINK = "-";
    public static final String DEFAULT_UPLOAD_BASE_DIR = "uploads";
    public static final long MAX_UPLOAD_SIZE = 100 * 1024 * 1024L;


    /**
     * 表示List中只有一个元素
     */
    public static final int SINGLETON_LIST_SIZE = 1;

    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";
}
