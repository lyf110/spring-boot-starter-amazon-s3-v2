package com.amazon.s3.v2.model.enums;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuyangfang
 * @description 支持S3标准的OSS对象存储服务器
 * @since 2023/6/5 13:13:24
 */
public enum StorageTypeEnum {
    /**
     * 当前系统支持的所有存储源类型
     */
    LOCAL("local", "本地存储"),
    ALIYUN("aliyun", "阿里云 OSS"),
    WEBDAV("webdav", "WebDAV"),
    TENCENT("tencent", "腾讯云 COS"),
    UPYUN("upyun", "又拍云 USS"),
    FTP("ftp", "FTP"),
    SFTP("sftp", "SFTP"),
    HUAWEI("huawei", "华为云 OBS"),
    MINIO("minio", "MINIO"),
    S3("s3", "S3通用协议"),
    ONE_DRIVE("onedrive", "OneDrive"),
    ONE_DRIVE_CHINA("onedrive-china", "OneDrive 世纪互联"),
    SHAREPOINT_DRIVE("sharepoint", "SharePoint"),
    SHAREPOINT_DRIVE_CHINA("sharepoint-china", "SharePoint 世纪互联"),
    GOOGLE_DRIVE("google-drive", "Google Drive"),
    QINIU("qiniu", "七牛云 KODO"),
    DOGE_CLOUD("doge-cloud", "多吉云");

    private static final Map<String, StorageTypeEnum> ENUM_MAP = new HashMap<>();

    static {
        for (StorageTypeEnum type : StorageTypeEnum.values()) {
            ENUM_MAP.put(type.getKey(), type);
        }
    }


    private final String key;


    private final String description;

    StorageTypeEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    @JsonIgnore
    public String getValue() {
        return key;
    }
}
