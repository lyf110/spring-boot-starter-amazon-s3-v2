package com.amazon.s3.v2.utils;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.CORSRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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


    /**
     * 将xml转成CORSRule集合
     * <p>
     * <?xml version="1.0" encoding="UTF-8"?>
     * <CORSConfiguration xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
     * <CORSRule>
     * <ID>ecf61fee281342d69005831895e36afd</ID>
     * <AllowedHeader>*</AllowedHeader>
     * <AllowedMethod>PUT</AllowedMethod>
     * <AllowedMethod>POST</AllowedMethod>
     * <AllowedMethod>DELETE</AllowedMethod>
     * <AllowedOrigin>http://www.example.com</AllowedOrigin>
     * <AllowedOrigin>http://www.example2.com</AllowedOrigin>
     * <ExposeHeader>x-amz-server-side-encryption</ExposeHeader>
     * <ExposeHeader>x-amz-request-id</ExposeHeader>
     * <ExposeHeader>x-amz-id-2</ExposeHeader>
     * <MaxAgeSeconds>3000</MaxAgeSeconds>
     * </CORSRule>
     *
     * <CORSRule>
     * <ID>ecf61fee281342d69004323232436afd</ID>
     * <AllowedHeader>*</AllowedHeader>
     * <AllowedMethod>PUT</AllowedMethod>
     * <AllowedMethod>POST</AllowedMethod>
     * <AllowedMethod>DELETE</AllowedMethod>
     * <AllowedOrigin>http://www.example.com</AllowedOrigin>
     * <AllowedOrigin>http://www.example2.com</AllowedOrigin>
     * <ExposeHeader>x-amz-server-side-encryption</ExposeHeader>
     * <ExposeHeader>x-amz-request-id</ExposeHeader>
     * <ExposeHeader>x-amz-id-2</ExposeHeader>
     * <MaxAgeSeconds>3000</MaxAgeSeconds>
     * </CORSRule>
     * </CORSConfiguration>
     *
     * @param xmlCorsRules xmlCorsRules
     * @return CORSRule List
     */
    public static List<CORSRule> xmlToCorsRules(String xmlCorsRules) {
        try {
            Map<String, Object> objectMap = XmlUtil.xmlToMap(xmlCorsRules);
            if (MapUtil.isEmpty(objectMap)) {
                return Collections.emptyList();
            }

            Object corsRuleObj = objectMap.get("CORSRule");
            if (corsRuleObj instanceof List) {
                // 这里就是List<CORSRule>
                List<Map<?, ?>> corsRuleObjList = (List<Map<?, ?>>) corsRuleObj;
                return corsRuleObjList.stream().map(BucketUtil::mapToCorsRule).collect(Collectors.toList());
            } else if (corsRuleObj instanceof Map) {
                // 这里就是CORSRule
                Map<?, ?> corsRuleObjMap = (Map<?, ?>) corsRuleObj;
                return Collections.singletonList(mapToCorsRule(corsRuleObjMap));
            } else {
                throw new IllegalArgumentException(String.format("corsRuleObj [%s] type [%s] is Unsupported types", corsRuleObj, corsRuleObj.getClass()));
            }
        } catch (Exception e) {
            log.error("xml [{}] To Cors Rules failed, the cause is", xmlCorsRules, e);
            return Collections.emptyList();
        }
    }

    /**
     * [
     * {
     * "AllowedMethod" : [
     * "PUT",
     * "POST",
     * "DELETE"
     * ],
     * "MaxAgeSeconds" : "3000",
     * "ExposeHeader" : [
     * "x-amz-server-side-encryption",
     * "x-amz-request-id",
     * "x-amz-id-2"
     * ],
     * "ID" : "ecf61fee281342d69005831895e36afd",
     * "AllowedOrigin" : [
     * "http://www.example.com",
     * "http://www.example2.com"
     * ],
     * "AllowedHeader" : "*"
     * },
     * {
     * "AllowedMethod" : [
     * "PUT",
     * "POST",
     * "DELETE"
     * ],
     * "MaxAgeSeconds" : "3000",
     * "ExposeHeader" : [
     * "x-amz-server-side-encryption",
     * "x-amz-request-id",
     * "x-amz-id-2"
     * ],
     * "ID" : "ecf61fee281342d69004323232436afd",
     * "AllowedOrigin" : [
     * "http://www.example.com",
     * "http://www.example2.com"
     * ],
     * "AllowedHeader" : "*"
     * }
     * ]
     *
     * @param jsonCorsRules jsonCorsRules
     * @return CORSRule List
     */
    public static List<CORSRule> jsonToCorsRules(String jsonCorsRules) {
        try {
            // 尝试转成JSONArray
            JSONArray jsonArray = JSONUtil.parseArray(jsonCorsRules);

            List<CORSRule> corsRuleList = new ArrayList<>(jsonArray.size());
            for (Object obj : jsonArray) {
                Map<?, ?> map = (Map<?, ?>) obj;
                CORSRule corsRule = mapToCorsRule(map);
                corsRuleList.add(corsRule);
            }

            return corsRuleList;
        } catch (Exception e) {
            log.error("{} parse to json array failed, the cause is {}", jsonCorsRules, e.getMessage());

            try {
                JSONObject jsonObject = JSONUtil.parseObj(jsonCorsRules);
                return Collections.singletonList(mapToCorsRule(jsonObject));
            } catch (Exception ex) {
                log.error("{} parse to json object failed, the cause is {}", jsonCorsRules, ex.getMessage());

                return Collections.emptyList();
            }
        }
    }

    /**
     * 将Map转成CORSRule
     *
     * @param corsRuleObjMap corsRuleObjMap
     * @return CORSRule
     */
    @SuppressWarnings("unchecked")
    private static CORSRule mapToCorsRule(Map<?, ?> corsRuleObjMap) {
        CORSRule.Builder builder = CORSRule.builder();
        corsRuleObjMap.forEach((key, value) -> {
            switch (key.toString()) {
                case "AllowedMethod":
                    if (value instanceof List) {
                        List<String> allowedMethods = (List<String>) value;
                        builder.allowedMethods(allowedMethods);
                    } else if (value instanceof String) {
                        builder.allowedMethods(Collections.singletonList(((String) value)));
                    } else {
                        throw new IllegalArgumentException(String.format("AllowedMethod [%s] Format error", value));
                    }
                    break;
                case "ExposeHeader":
                    if (value instanceof List) {
                        List<String> exposeHeaders = (List<String>) value;
                        builder.exposeHeaders(exposeHeaders);
                    } else if (value instanceof String) {
                        builder.exposeHeaders(Collections.singletonList(((String) value)));
                    } else {
                        throw new IllegalArgumentException(String.format("ExposeHeader [%s] Format error", value));
                    }
                    break;
                case "AllowedHeader":
                    if (value instanceof List) {
                        List<String> allowedHeaders = (List<String>) value;
                        builder.allowedHeaders(allowedHeaders);
                    } else if (value instanceof String) {
                        builder.allowedHeaders(Collections.singletonList(((String) value)));
                    } else {
                        throw new IllegalArgumentException(String.format("AllowedHeader [%s] Format error", value));
                    }
                    break;
                case "AllowedOrigin":
                    if (value instanceof List) {
                        List<String> allowedOrigins = (List<String>) value;
                        builder.allowedOrigins(allowedOrigins);
                    } else if (value instanceof String) {
                        builder.allowedOrigins(Collections.singletonList(((String) value)));
                    } else {
                        throw new IllegalArgumentException(String.format("AllowedOrigin [%s] Format error", value));
                    }
                    break;
                case "ID":
                    if (value instanceof String) {
                        builder.id((String) value);
                    } else {
                        throw new IllegalArgumentException(String.format("ID [%s] not String", value));
                    }
                    break;
                case "MaxAgeSeconds":
                    if (value instanceof Integer) {
                        builder.maxAgeSeconds((Integer) value);
                    } else if (NumberUtil.isNumber(value.toString())) {
                        builder.maxAgeSeconds(Integer.parseInt((String) value));
                    } else {
                        throw new IllegalArgumentException(String.format("MaxAgeSeconds [%s] not number", value));
                    }
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown cors config item [%s]", key));
            }
        });

        return builder.build();
    }
}
