package com.amazon.s3.v2.model.enums.oss;

import com.amazon.s3.v2.model.enums.IEnum;
import lombok.Getter;

import static com.amazon.s3.v2.constant.BusinessV2Constant.HTTPS_PREFIX;
import static com.amazon.s3.v2.constant.BusinessV2Constant.HTTP_PREFIX;

/**
 * @author liuyangfang
 * @description 华为云OBS支持的Endpoint
 * 地址：https://developer.huaweicloud.com/endpoint?OBS
 * <p>
 * 非洲-约翰内斯堡	af-south-1	obs.af-south-1.myhuaweicloud.com	HTTPS/HTTP
 * 华北-北京四	cn-north-4	obs.cn-north-4.myhuaweicloud.com	HTTPS/HTTP
 * 华北-北京一	cn-north-1	obs.cn-north-1.myhuaweicloud.com	HTTPS/HTTP
 * 华北-乌兰察布一	cn-north-9	obs.cn-north-9.myhuaweicloud.com	HTTPS/HTTP
 * 华东-上海二	cn-east-2	obs.cn-east-2.myhuaweicloud.com	HTTPS/HTTP
 * 华东-上海一	cn-east-3	obs.cn-east-3.myhuaweicloud.com	HTTPS/HTTP
 * 华南-广州	cn-south-1	obs.cn-south-1.myhuaweicloud.com	HTTPS/HTTP
 * 华南-广州-友好用户环境	cn-south-4	obs.cn-south-4.myhuaweicloud.com	HTTPS/HTTP
 * 拉美-墨西哥城二	la-north-2	obs.la-north-2.myhuaweicloud.com	HTTPS/HTTP
 * 拉美-墨西哥城一	na-mexico-1	obs.na-mexico-1.myhuaweicloud.com	HTTPS/HTTP
 * 拉美-圣保罗一	sa-brazil-1	obs.sa-brazil-1.myhuaweicloud.com	HTTPS/HTTP
 * 拉美-圣地亚哥	la-south-2	obs.la-south-2.myhuaweicloud.com	HTTPS/HTTP
 * 土耳其-伊斯坦布尔	tr-west-1	obs.tr-west-1.myhuaweicloud.com	HTTPS/HTTP
 * 西南-贵阳一	cn-southwest-2	obs.cn-southwest-2.myhuaweicloud.com	HTTPS/HTTP
 * 亚太-曼谷	ap-southeast-2	obs.ap-southeast-2.myhuaweicloud.com	HTTPS/HTTP
 * 亚太-新加坡	ap-southeast-3	obs.ap-southeast-3.myhuaweicloud.com	HTTPS/HTTP
 * 中国-香港	ap-southeast-1	obs.ap-southeast-1.myhuaweicloud.com	HTTPS/HTTP
 * @since 2023/6/5 14:06:55
 */
@Getter
public enum HuaWeiCloudObsS3Enum implements IEnum<String> {
    AF_SOUTH_1("af-south-1", "obs.af-south-1.myhuaweicloud.com", "非洲-约翰内斯堡"),
    CN_NORTH_4("cn-north-4", "obs.cn-north-4.myhuaweicloud.com", "华北-北京四"),
    CN_NORTH_1("cn-north-1", "obs.cn-north-1.myhuaweicloud.com", "华北-北京一"),
    CN_NORTH_9("cn-north-9", "obs.cn-north-9.myhuaweicloud.com", "华北-乌兰察布一"),
    CN_EAST_2("cn-east-2", "obs.cn-east-2.myhuaweicloud.com", "华东-上海二"),
    CN_EAST_3("cn-east-3", "obs.cn-east-3.myhuaweicloud.com", "华东-上海一"),
    CN_SOUTH_1("cn-south-1", "obs.cn-south-1.myhuaweicloud.com", "华南-广州"),
    CN_SOUTH_4("cn-south-4", "obs.cn-south-4.myhuaweicloud.com", "华南-广州-友好用户环境"),
    LA_NORTH_2("la-north-2", "obs.la-north-2.myhuaweicloud.com", "拉美-墨西哥城二"),
    NA_MEXICO_1("na-mexico-1", "obs.na-mexico-1.myhuaweicloud.com", "拉美-墨西哥城一"),
    SA_BRAZIL_1("sa-brazil-1", "obs.sa-brazil-1.myhuaweicloud.com", "拉美-圣保罗一"),
    LA_SOUTH_2("la-south-2", "obs.la-south-2.myhuaweicloud.com", "拉美-圣地亚哥"),
    TR_WEST_1("tr-west-1", "obs.tr-west-1.myhuaweicloud.com", "土耳其-伊斯坦布尔"),
    CN_SOUTHWEST_2("cn-southwest-2", "obs.cn-southwest-2.myhuaweicloud.com", "西南-贵阳一"),
    AP_SOUTHEAST_2("ap-southeast-2", "obs.ap-southeast-2.myhuaweicloud.com", "亚太-曼谷"),
    AP_SOUTHEAST_3("ap-southeast-3", "obs.ap-southeast-3.myhuaweicloud.com", "亚太-新加坡"),
    AP_SOUTHEAST_1("ap-southeast-1", "obs.ap-southeast-1.myhuaweicloud.com", "中国-香港");

    private final String region;
    private final String endpoint;
    private final String description;

    HuaWeiCloudObsS3Enum(String region, String endpoint, String description) {
        this.region = region;
        this.endpoint = endpoint;
        this.description = description;
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
