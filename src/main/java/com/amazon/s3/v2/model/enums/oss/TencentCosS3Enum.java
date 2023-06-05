package com.amazon.s3.v2.model.enums.oss;

import com.amazon.s3.v2.model.enums.IEnum;
import lombok.Getter;

import static com.amazon.s3.v2.constant.BusinessV2Constant.HTTPS_PREFIX;
import static com.amazon.s3.v2.constant.BusinessV2Constant.HTTP_PREFIX;

/**
 * @author liuyangfang
 * @description
 * @since 2023/6/5 16:07:17
 */
public enum TencentCosS3Enum implements IEnum<String> {
    // 中国本地
    AP_NANJING("ap-nanjing", "cos.ap-nanjing.myqcloud.com", "南京", "中国"),
    AP_CHENGDU("ap-chengdu", "cos.ap-chengdu.myqcloud.com", "成都", "中国"),
    AP_BEIJING("ap-beijing", "cos.ap-beijing.myqcloud.com", "北京", "中国"),
    AP_GUANGZHOU("ap-guangzhou", "cos.ap-guangzhou.myqcloud.com", "广州", "中国"),
    AP_SHANGHAI("ap-shanghai", "cos.ap-shanghai.myqcloud.com", "上海", "中国"),
    AP_CHONGQING("ap-chongqing", "cos.ap-chongqing.myqcloud.com", "重庆", "中国"),
    AP_HONGKONG("ap-hongkong", "cos.ap-hongkong.myqcloud.com", "中国香港", "中国"),

    // 金融相关和普通OSS相互隔离
    AP_BEIJING_FSI("ap-beijing-fsi", "cos.ap-beijing-fsi.myqcloud.com", "北京金融", "中国"),
    AP_SHANGHAI_FSI("ap-shanghai-fsi", "cos.ap-shanghai-fsi.myqcloud.com", "上海金融", "中国"),
    AP_SHENZHEN_FSI("ap-shenzhen-fsi", "cos.ap-shenzhen-fsi.myqcloud.com", "深圳金融", "中国"),

    // 亚太地区
    AP_SINGAPORE("ap-singapore", "cos.ap-singapore.myqcloud.com", "新加坡", "亚太地区"),
    AP_BANGKOK("ap-bangkok", "cos.ap-bangkok.myqcloud.com", "曼谷", "亚太地区"),
    AP_JAKARTA("ap-jakarta", "cos.ap-jakarta.myqcloud.com", "雅加达", "亚太地区"),
    AP_MUMBAI("ap-mumbai", "cos.ap-mumbai.myqcloud.com", "孟买", "亚太地区"),
    AP_TOKYO("ap-tokyo", "cos.ap-tokyo.myqcloud.com", "东京", "亚太地区"),
    AP_SEOUL("ap-seoul", "cos.ap-seoul.myqcloud.com", "首尔", "亚太地区"),

    // 欧洲地区
    EU_FRANKFURT("eu-frankfurt", "cos.eu-frankfurt.myqcloud.com", "法兰克福", "欧洲地区"),

    // 北美地区
    NA_TORONTO("na-toronto", "cos.na-toronto.myqcloud.com", "多伦多", "北美地区"),
    NA_ASHBURN("na-ashburn", "cos.na-ashburn.myqcloud.com", "弗吉尼亚", "北美地区"),
    NA_SILICONVALLEY("na-siliconvalley", "cos.na-siliconvalley.myqcloud.com", "硅谷", "北美地区"),

    // 南美地区
    SA_SAOPAULO("sa-saopaulo", "cos.sa-saopaulo.myqcloud.com", "圣保罗", "南美地区");

    /**
     * 所属区域
     */
    private final String region;

    /**
     * 访问的网址
     */
    private final String endpoint;

    /**
     * 区域的中文描述
     */
    @Getter
    private final String description;

    /**
     * 所属国家或者地区
     */
    @Getter
    private final String country;

    TencentCosS3Enum(String region, String endpoint, String description, String country) {
        this.region = region;
        this.endpoint = endpoint;
        this.description = description;
        this.country = country;
    }

    @Override
    public String getValue() {
        return region;
    }

    @Override
    public String getHttpEndpoint() {
        return HTTP_PREFIX + endpoint;
    }

    @Override
    public String getHttpsEndpoint() {
        return HTTPS_PREFIX + endpoint;
    }
}
