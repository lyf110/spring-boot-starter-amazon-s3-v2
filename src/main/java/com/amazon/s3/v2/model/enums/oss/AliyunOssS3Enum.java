package com.amazon.s3.v2.model.enums.oss;

import com.amazon.s3.v2.model.enums.IEnum;
import lombok.Getter;

import static com.amazon.s3.v2.constant.BusinessV2Constant.HTTPS_PREFIX;
import static com.amazon.s3.v2.constant.BusinessV2Constant.HTTP_PREFIX;

/**
 * @author liuyangfang
 * @description Region
 * 来源地址：
 * https://help.aliyun.com/document_detail/31837.html?spm=a2c4g.161787.0.0.7ac51adcEwxOiM#section-oao-7ao-11f
 * <p>
 * Region ID
 * <p>
 * 外网Endpoint
 * <p>
 * 内网Endpoint
 * <p>
 * 华东1（杭州）
 * <p>
 * oss-cn-hangzhou
 * <p>
 * oss-cn-hangzhou.aliyuncs.com
 * <p>
 * 说明 oss.aliyuncs.com默认指向华东1（杭州）地域外网地址。
 * oss-cn-hangzhou-internal.aliyuncs.com
 * <p>
 * 说明 oss-internal.aliyuncs.com默认指向华东1（杭州）地域内网地址。
 * 华东2（上海）
 * <p>
 * oss-cn-shanghai
 * <p>
 * oss-cn-shanghai.aliyuncs.com
 * <p>
 * oss-cn-shanghai-internal.aliyuncs.com
 * <p>
 * 华东5（南京-本地地域）
 * <p>
 * oss-cn-nanjing
 * <p>
 * oss-cn-nanjing.aliyuncs.com
 * <p>
 * oss-cn-nanjing-internal.aliyuncs.com
 * <p>
 * 华东6（福州-本地地域）
 * <p>
 * oss-cn-fuzhou
 * <p>
 * oss-cn-fuzhou.aliyuncs.com
 * <p>
 * oss-cn-fuzhou-internal.aliyuncs.com
 * <p>
 * 华北1（青岛）
 * <p>
 * oss-cn-qingdao
 * <p>
 * oss-cn-qingdao.aliyuncs.com
 * <p>
 * oss-cn-qingdao-internal.aliyuncs.com
 * <p>
 * 华北2（北京）
 * <p>
 * oss-cn-beijing
 * <p>
 * oss-cn-beijing.aliyuncs.com
 * <p>
 * oss-cn-beijing-internal.aliyuncs.com
 * <p>
 * 华北 3（张家口）
 * <p>
 * oss-cn-zhangjiakou
 * <p>
 * oss-cn-zhangjiakou.aliyuncs.com
 * <p>
 * oss-cn-zhangjiakou-internal.aliyuncs.com
 * <p>
 * 华北5（呼和浩特）
 * <p>
 * oss-cn-huhehaote
 * <p>
 * oss-cn-huhehaote.aliyuncs.com
 * <p>
 * oss-cn-huhehaote-internal.aliyuncs.com
 * <p>
 * 华北6（乌兰察布）
 * <p>
 * oss-cn-wulanchabu
 * <p>
 * oss-cn-wulanchabu.aliyuncs.com
 * <p>
 * oss-cn-wulanchabu-internal.aliyuncs.com
 * <p>
 * 华南1（深圳）
 * <p>
 * oss-cn-shenzhen
 * <p>
 * oss-cn-shenzhen.aliyuncs.com
 * <p>
 * oss-cn-shenzhen-internal.aliyuncs.com
 * <p>
 * 华南2（河源）
 * <p>
 * oss-cn-heyuan
 * <p>
 * oss-cn-heyuan.aliyuncs.com
 * <p>
 * oss-cn-heyuan-internal.aliyuncs.com
 * <p>
 * 华南3（广州）
 * <p>
 * oss-cn-guangzhou
 * <p>
 * oss-cn-guangzhou.aliyuncs.com
 * <p>
 * oss-cn-guangzhou-internal.aliyuncs.com
 * <p>
 * 西南1（成都）
 * <p>
 * oss-cn-chengdu
 * <p>
 * oss-cn-chengdu.aliyuncs.com
 * <p>
 * oss-cn-chengdu-internal.aliyuncs.com
 * <p>
 * 中国香港
 * <p>
 * oss-cn-hongkong
 * <p>
 * oss-cn-hongkong.aliyuncs.com
 * <p>
 * oss-cn-hongkong-internal.aliyuncs.com
 * <p>
 * 美国（硅谷）①
 * <p>
 * oss-us-west-1
 * <p>
 * oss-us-west-1.aliyuncs.com
 * <p>
 * oss-us-west-1-internal.aliyuncs.com
 * <p>
 * 美国（弗吉尼亚）①
 * <p>
 * oss-us-east-1
 * <p>
 * oss-us-east-1.aliyuncs.com
 * <p>
 * oss-us-east-1-internal.aliyuncs.com
 * <p>
 * 日本（东京）①
 * <p>
 * oss-ap-northeast-1
 * <p>
 * oss-ap-northeast-1.aliyuncs.com
 * <p>
 * oss-ap-northeast-1-internal.aliyuncs.com
 * <p>
 * 韩国（首尔）
 * <p>
 * oss-ap-northeast-2
 * <p>
 * oss-ap-northeast-2.aliyuncs.com
 * <p>
 * oss-ap-northeast-2-internal.aliyuncs.com
 * <p>
 * 新加坡①
 * <p>
 * oss-ap-southeast-1
 * <p>
 * oss-ap-southeast-1.aliyuncs.com
 * <p>
 * oss-ap-southeast-1-internal.aliyuncs.com
 * <p>
 * 澳大利亚（悉尼）①
 * <p>
 * oss-ap-southeast-2
 * <p>
 * oss-ap-southeast-2.aliyuncs.com
 * <p>
 * oss-ap-southeast-2-internal.aliyuncs.com
 * <p>
 * 马来西亚（吉隆坡）①
 * <p>
 * oss-ap-southeast-3
 * <p>
 * oss-ap-southeast-3.aliyuncs.com
 * <p>
 * oss-ap-southeast-3-internal.aliyuncs.com
 * <p>
 * 印度尼西亚（雅加达）①
 * <p>
 * oss-ap-southeast-5
 * <p>
 * oss-ap-southeast-5.aliyuncs.com
 * <p>
 * oss-ap-southeast-5-internal.aliyuncs.com
 * <p>
 * 菲律宾（马尼拉）
 * <p>
 * oss-ap-southeast-6
 * <p>
 * oss-ap-southeast-6.aliyuncs.com
 * <p>
 * oss-ap-southeast-6-internal.aliyuncs.com
 * <p>
 * 泰国（曼谷）
 * <p>
 * oss-ap-southeast-7
 * <p>
 * oss-ap-southeast-7.aliyuncs.com
 * <p>
 * oss-ap-southeast-7-internal.aliyuncs.com
 * <p>
 * 印度（孟买）①
 * <p>
 * oss-ap-south-1
 * <p>
 * oss-ap-south-1.aliyuncs.com
 * <p>
 * oss-ap-south-1-internal.aliyuncs.com
 * <p>
 * 德国（法兰克福）①
 * <p>
 * oss-eu-central-1
 * <p>
 * oss-eu-central-1.aliyuncs.com
 * <p>
 * oss-eu-central-1-internal.aliyuncs.com
 * <p>
 * 英国（伦敦）
 * <p>
 * oss-eu-west-1
 * <p>
 * oss-eu-west-1.aliyuncs.com
 * <p>
 * oss-eu-west-1-internal.aliyuncs.com
 * <p>
 * 阿联酋（迪拜）①
 * <p>
 * oss-me-east-1
 * <p>
 * oss-me-east-1.aliyuncs.com
 * <p>
 * oss-me-east-1-internal.aliyuncs.com
 * <p>
 * 无地域属性（中国内地）
 * <p>
 * oss-rg-china-mainland
 * <p>
 * oss-rg-china-mainland.aliyuncs.com
 * <p>
 * 不支持
 * @since 2023/6/5 13:36:37
 */
@Getter
public enum AliyunOssS3Enum implements IEnum<String> {
    OSS_CN_HANGZHOU("oss-cn-hangzhou", "oss-cn-hangzhou.aliyuncs.com", "oss-cn-hangzhou-internal.aliyuncs.com", "华东1（杭州）"),
    OSS_CN_SHANGHAI("oss-cn-shanghai", "oss-cn-shanghai.aliyuncs.com", "oss-cn-shanghai-internal.aliyuncs.com", "华东2（上海）"),
    OSS_CN_NANJING("oss-cn-nanjing", "oss-cn-nanjing.aliyuncs.com", "oss-cn-nanjing-internal.aliyuncs.com", "华东5（南京-本地地域）"),
    OSS_CN_FUZHOU("oss-cn-fuzhou", "oss-cn-fuzhou.aliyuncs.com", "oss-cn-fuzhou-internal.aliyuncs.com", "华东6（福州-本地地域）"),
    OSS_CN_QINGDAO("oss-cn-qingdao", "oss-cn-qingdao.aliyuncs.com", "oss-cn-qingdao-internal.aliyuncs.com", "华北1（青岛）"),
    OSS_CN_BEIJING("oss-cn-beijing", "oss-cn-beijing.aliyuncs.com", "oss-cn-beijing-internal.aliyuncs.com", "华北2（北京）"),
    OSS_CN_ZHANGJIAKOU("oss-cn-zhangjiakou", "oss-cn-zhangjiakou.aliyuncs.com", "oss-cn-zhangjiakou-internal.aliyuncs.com", "华北 3（张家口）"),
    OSS_CN_HUHEHAOTE("oss-cn-huhehaote", "oss-cn-huhehaote.aliyuncs.com", "oss-cn-huhehaote-internal.aliyuncs.com", "华北5（呼和浩特）"),
    OSS_CN_WULANCHABU("oss-cn-wulanchabu", "oss-cn-wulanchabu.aliyuncs.com", "oss-cn-wulanchabu-internal.aliyuncs.com", "华北6（乌兰察布）"),
    OSS_CN_SHENZHEN("oss-cn-shenzhen", "oss-cn-shenzhen.aliyuncs.com", "oss-cn-shenzhen-internal.aliyuncs.com", "华南1（深圳）"),
    OSS_CN_HEYUAN("oss-cn-heyuan", "oss-cn-heyuan.aliyuncs.com", "oss-cn-heyuan-internal.aliyuncs.com", "华南2（河源）"),
    OSS_CN_GUANGZHOU("oss-cn-guangzhou", "oss-cn-guangzhou.aliyuncs.com", "oss-cn-guangzhou-internal.aliyuncs.com", "华南3（广州）"),
    OSS_CN_CHENGDU("oss-cn-chengdu", "oss-cn-chengdu.aliyuncs.com", "oss-cn-chengdu-internal.aliyuncs.com", "西南1（成都）"),
    OSS_CN_HONGKONG("oss-cn-hongkong", "oss-cn-hongkong.aliyuncs.com", "oss-cn-hongkong-internal.aliyuncs.com", "中国香港"),
    OSS_US_WEST_1("oss-us-west-1", "oss-us-west-1.aliyuncs.com", "oss-us-west-1-internal.aliyuncs.com", "美国（硅谷）①"),
    OSS_US_EAST_1("oss-us-east-1", "oss-us-east-1.aliyuncs.com", "oss-us-east-1-internal.aliyuncs.com", "美国（弗吉尼亚）①"),
    OSS_AP_NORTHEAST_1("oss-ap-northeast-1", "oss-ap-northeast-1.aliyuncs.com", "oss-ap-northeast-1-internal.aliyuncs.com", "日本（东京）①"),
    OSS_AP_NORTHEAST_2("oss-ap-northeast-2", "oss-ap-northeast-2.aliyuncs.com", "oss-ap-northeast-2-internal.aliyuncs.com", "韩国（首尔）"),
    OSS_AP_SOUTHEAST_1("oss-ap-southeast-1", "oss-ap-southeast-1.aliyuncs.com", "oss-ap-southeast-1-internal.aliyuncs.com", "新加坡①"),
    OSS_AP_SOUTHEAST_2("oss-ap-southeast-2", "oss-ap-southeast-2.aliyuncs.com", "oss-ap-southeast-2-internal.aliyuncs.com", "澳大利亚（悉尼）①"),
    OSS_AP_SOUTHEAST_3("oss-ap-southeast-3", "oss-ap-southeast-3.aliyuncs.com", "oss-ap-southeast-3-internal.aliyuncs.com", "马来西亚（吉隆坡）①"),
    OSS_AP_SOUTHEAST_5("oss-ap-southeast-5", "oss-ap-southeast-5.aliyuncs.com", "oss-ap-southeast-5-internal.aliyuncs.com", "印度尼西亚（雅加达）①"),
    OSS_AP_SOUTHEAST_6("oss-ap-southeast-6", "oss-ap-southeast-6.aliyuncs.com", "oss-ap-southeast-6-internal.aliyuncs.com", "菲律宾（马尼拉）"),
    OSS_AP_SOUTHEAST_7("oss-ap-southeast-7", "oss-ap-southeast-7.aliyuncs.com", "oss-ap-southeast-7-internal.aliyuncs.com", "泰国（曼谷）"),
    OSS_AP_SOUTH_1("oss-ap-south-1", "oss-ap-south-1.aliyuncs.com", "oss-ap-south-1-internal.aliyuncs.com", "印度（孟买）①"),
    OSS_EU_CENTRAL_1("oss-eu-central-1", "oss-eu-central-1.aliyuncs.com", "oss-eu-central-1-internal.aliyuncs.com", "德国（法兰克福）①"),
    OSS_EU_WEST_1("oss-eu-west-1", "oss-eu-west-1.aliyuncs.com", "oss-eu-west-1-internal.aliyuncs.com", "英国（伦敦）"),
    OSS_ME_EAST_1("oss-me-east-1", "oss-me-east-1.aliyuncs.com", "oss-me-east-1-internal.aliyuncs.com", "阿联酋（迪拜）①"),
    OSS_RG_CHINA_MAINLAND("oss-rg-china-mainland", "oss-rg-china-mainland.aliyuncs.com", "不支持", "无地域属性（中国内地）");

    /**
     * 存储的地域信息
     */
    private final String region;

    /**
     * 外网访问的地址
     */
    private final String outEndpoint;

    /**
     * 内网访问的地址
     */
    private final String innerEndpoint;

    /**
     * 存储的地域信息的中文描述
     */
    private final String description;

    AliyunOssS3Enum(String region, String outEndpoint, String innerEndpoint, String description) {
        this.region = region;
        this.outEndpoint = outEndpoint;
        this.innerEndpoint = innerEndpoint;
        this.description = description;
    }

    @Override
    public String getValue() {
        return region;
    }

    @Override
    public String getHttpEndpoint() {
        return HTTP_PREFIX + outEndpoint;
    }

    @Override
    public String getHttpsEndpoint() {
        return HTTPS_PREFIX + outEndpoint;
    }
}
