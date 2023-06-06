package com.amazon.s3.v2.config;

import cn.hutool.core.lang.Assert;
import com.amazon.s3.v2.constant.BusinessV2Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author liuyangfang
 * @description
 * @since 2023/6/5 10:28:36
 */
@ConfigurationProperties(value = "amazon.s3.v2.oss")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class S3V2Base implements Serializable {
    private static final long serialVersionUID = 4391198558388776838L;

    /**
     * 存储服务器所在地址
     */
    private String endPoint;

    /**
     * 访问凭证名
     */
    private String accessKey;

    /**
     * 访问凭证秘钥
     */
    private String secretKey;

    /**
     * bucketName是你设置的桶的默认桶的名称
     */
    private String bucket;

    /**
     * 腾讯云COS需要填写AppleID
     */
    private String appleId;

    /**
     * Bucket域名或者CDN加速域名
     */
    private String domain;

    /**
     * 基路径表示读取的根文件夹，不填写表示允许读取所有。如： '/'，'/文件夹1'"
     */
    private String basePath;

    /**
     * 是否是私有空间
     * 私有空间会生成带签名的下载链接
     */
    private boolean isPrivate;

    /**
     * 下载签名有效期
     * 当为私有空间时, 用于下载签名的有效期, 单位为秒, 如不配置则默认为 1800 秒.
     */
    private Integer tokenTime;

    /**
     * 存储服务器所在的区域
     */
    private String region;


    /**
     * 是否自动配置 CORS 跨域设置
     * 如不配置跨域设置，可能会无法导致无法上传，或上传后看不到文件
     * （某些 S3 存储无需配置此选项，如 Cloudflare R2、Oracle 对象存储）
     */
    private boolean autoConfigCors;

    /**
     * 提供一个方法用来判断是否是腾讯云COS
     *
     * @return true: 是腾讯云COS，false则不是
     */
    public boolean isTencentCos() {
        Assert.notEmpty(endPoint, "endPoint not empty");
        return endPoint.toLowerCase(Locale.ENGLISH).endsWith(BusinessV2Constant.TENCENT_COS_ENDPOINT_SUFFIX);
    }
}
