package com.amazon.s3.v2.utils;


import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * @author liuyangfang
 * @description 处理桶的工具类
 * @since 2023/6/6 16:54:57
 */
@Slf4j
public final class BucketUtil {
    /**
     * 存储桶名称只能由小写字母、数字、句点 (.) 和连字符 (-) 组成。
     * 存储桶名称必须以字母或数字开头和结尾。
     */
    private static final String REGEX = "^[a-z0-9][a-z0-9.-]*[a-z0-9]$";

    /**
     * 存储桶名称不得包含两个相邻的句点。
     */
    private static final String REGEX2 = "!.*?..";

    private static final String IPV4_REGEX = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private static final String IPV6_REGEX = "^(?:[A-F0-9]{1,4}:){7}[A-F0-9]{1,4}$";
    private static final String HOST_REGEX = "^(?:[a-z0-9-]+\\.)+[a-z0-9-]+$";

    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private static final Pattern PATTERN2 = Pattern.compile(REGEX2);
    private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);
    private static final Pattern IPV6_PATTERN = Pattern.compile(IPV6_REGEX);
    private static final Pattern HOST_PATTERN = Pattern.compile(HOST_REGEX);

    private BucketUtil() {
    }

    /**
     * 存储桶命名有效
     * 判断规则：
     * 存储桶名称必须介于 3（最少）到 63（最多）个字符之间。
     * <p>
     * 存储桶名称只能由小写字母、数字、句点 (.) 和连字符 (-) 组成。
     * <p>
     * 存储桶名称必须以字母或数字开头和结尾。
     * <p>
     * 存储桶名称不得包含两个相邻的句点。
     * <p>
     * 存储桶名称不得采用 IP 地址格式（例如，192.168.5.4）。
     * <p>
     * 存储桶名称不得以前缀 xn-- 开头。
     * <p>
     * 存储桶名称不得以后缀 -s3alias 结尾。
     * 此后缀是为接入点别名预留的。有关更多信息，请参阅为您的 S3 桶接入点使用桶式别名。
     * <p>
     * 存储桶名称不得以后缀 --ol-s3 结尾。
     * 此后缀是为对象 Lambda 接入点别名预留的。有关更多信息，请参阅如何为您的 S3 桶对象 Lambda 接入点使用桶式别名。
     * <p>
     * 存储桶名称在分区内所有 AWS 区域中的所有 AWS 账户间必须是唯一的。
     * 分区是一组区域。AWS 目前有三个分区：aws（标准区域）、aws-cn（中国区域）和 aws-us-gov（AWS GovCloud (US) 区域）。
     * <p>
     * 存储桶名称不能被同一分区中的另一个 AWS 账户使用，直到存储桶被删除。
     * <p>
     * 与 Amazon S3 Transfer Acceleration 一起使用的存储桶名称中不能有句点 (.)。
     * 有关 Transfer Acceleration 的更多信息，
     * 请参阅使用 Amazon S3 Transfer Acceleration 配置快速、安全的文件传输。
     *
     * @param bucketName 默认的存储名称
     * @return true: 符合命名规范，false：不符合命名规范
     */
    public static boolean isValid(String bucketName) {
        // 非空校验
        if (StrUtil.isEmpty(bucketName)) {
            log.warn("bucketName not empty");
            return false;
        }

        // 存储桶名称必须介于 3（最少）到 63（最多）个字符之间。
        int length = bucketName.length();
        if (length < 3 || length > 63) {
            log.warn("bucket [{}] length must be 3~63", bucketName);
            return false;
        }

        // 存储桶名称只能由小写字母、数字、句点 (.) 和连字符 (-) 组成。
        // 存储桶名称必须以字母或数字开头和结尾。
        if (!PATTERN.matcher(bucketName).matches()) {
            log.warn("bucket [{}] can only consist of lowercase letters, numbers, periods (.), and hyphens (-) and" +
                    " must start and end with a letter or number.", bucketName);
            log.warn("bucket [{}] 存储桶名称只能由小写字母、数字、句点 (.) 和连字符 (-) 组成。存储桶名称必须以字母或数字开头和结尾。", bucketName);
            return false;
        }

        // 存储桶名称不得包含两个相邻的句点。
        if (PATTERN2.matcher(bucketName).matches()) {
            log.warn("bucket [{}] must not contain two adjacent periods (.)", bucketName);
            log.warn("bucket [{}] 存储桶名称不得包含两个相邻的句点(.)", bucketName);
            return false;
        }

        // 存储桶名称不得采用 IP 地址格式（例如，192.168.5.4）。
        if (IPV4_PATTERN.matcher(bucketName).matches()) {
            log.warn("bucket [{}] must not use of ipv4", bucketName);
            return false;
        }

        // 存储桶名称不得采用 IP 地址格式（例如，192.168.5.4）。
        if (IPV6_PATTERN.matcher(bucketName).matches()) {
            log.warn("bucket [{}] must not use of ipv6", bucketName);
            return false;
        }

//        // to resolve java.lang.NullPointerException: host must not be null.
//        if (HOST_PATTERN.matcher(bucketName).matches()) {
//            log.warn("bucket [{}] must not like host", bucketName);
//            return false;
//        }

        // 存储桶名称不得以前缀 xn-- 开头。
        // 存储桶名称不得以后缀 -s3alias 结尾。
        // 存储桶名称不得以后缀 --ol-s3 结尾。
        if (bucketName.startsWith("xn--") || bucketName.endsWith("-s3alias") || bucketName.endsWith("--ol-s3")) {
            log.warn("bucket [{}] must not start with [xn--] or end with [-s3alias] or end with [--ol-s3] ", bucketName);
            return false;
        }

        // 经过此前的判断，此时才是一个合法的存储桶的名称
        return true;
    }

    /**
     * bucket的名称是否为xxx.xxx.xxx这样的形式
     *
     * @param bucketName 桶名
     * @return true：满足xxx.xxx.xxx这样的形式， false：不满足xxx.xxx.xxx这样的形式
     */
    public static boolean isLikeHost(String bucketName) {
        return StrUtil.isNotEmpty(bucketName) && HOST_PATTERN.matcher(bucketName).matches();
    }
}
