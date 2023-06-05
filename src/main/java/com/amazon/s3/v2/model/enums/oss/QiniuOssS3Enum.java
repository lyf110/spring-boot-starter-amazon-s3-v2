package com.amazon.s3.v2.model.enums.oss;

import com.amazon.s3.v2.model.enums.IEnum;
import lombok.Getter;

import static com.amazon.s3.v2.constant.BusinessV2Constant.HTTPS_PREFIX;
import static com.amazon.s3.v2.constant.BusinessV2Constant.HTTP_PREFIX;

/**
 * @author liuyangfang
 * @description 七牛云与Amazon S3的绑定关系
 * 来源地址：
 * https://developer.qiniu.com/kodo/4088/s3-access-domainname
 * <p>
 * 华东-浙江	cn-east-1	s3-cn-east-1.qiniucs.com	HTTP，HTTPS
 * 华东-浙江2	cn-east-2	s3-cn-east-2.qiniucs.com	HTTP，HTTPS
 * 华北-河北	cn-north-1	s3-cn-north-1.qiniucs.com	HTTP，HTTPS
 * 华南-广东	cn-south-1	s3-cn-south-1.qiniucs.com	HTTP，HTTPS
 * 北美-洛杉矶	us-north-1	s3-us-north-1.qiniucs.com	HTTP，HTTPS
 * 亚太-新加坡（原东南亚）	ap-southeast-1	s3-ap-southeast-1.qiniucs.com	HTTP，HTTPS
 * @since 2023/6/5 13:17:15
 */
@Getter
public enum QiniuOssS3Enum implements IEnum<String> {
    CN_EAST_1("cn-east-1", "s3-cn-east-1.qiniucs.com", "华东-浙江"),
    CN_EAST_2("cn-east-2", "s3-cn-east-2.qiniucs.com", "华东-浙江2"),
    CN_NORTH_1("cn-north-1", "s3-cn-north-1.qiniucs.com", "华北-河北"),
    CN_SOUTH_1("cn-south-1", "s3-cn-south-1.qiniucs.com", "华南-广东"),
    US_NORTH_1("us-north-1", "s3-us-north-1.qiniucs.com", "北美-洛杉矶"),
    AP_SOUTHEAST_1("ap-southeast-1", "s3-ap-southeast-1.qiniucs.com", "亚太-新加坡（原东南亚）");

    /**
     * 存储区域
     */
    private final String region;

    /**
     * 访问的站点
     */
    private final String endPoint;

    /**
     * 存储区域中文描述
     */
    private final String description;

    QiniuOssS3Enum(String region, String endPoint, String description) {
        this.region = region;
        this.endPoint = endPoint;
        this.description = description;
    }

    @Override
    public String getValue() {
        return region;
    }

    @Override
    public String getHttpEndpoint() {
        return HTTP_PREFIX + endPoint;
    }

    @Override
    public String getHttpsEndpoint() {
        return HTTPS_PREFIX + endPoint;
    }
}
