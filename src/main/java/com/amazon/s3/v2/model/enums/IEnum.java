package com.amazon.s3.v2.model.enums;

import java.io.Serializable;

/**
 * @author liuyangfang
 * @description 枚举类的通用接口
 * @since 2023/6/5 13:18:13
 */
public interface IEnum<T extends Serializable> {
    /**
     * 枚举数据库存储值
     *
     * @return 数据库存储值
     */
    T getValue();

    /**
     * 获取Http形式的访问站点
     *
     * @return Http形式的访问站点
     */
    String getHttpEndpoint();

    /**
     * 获取Https形式的访问站点
     *
     * @return Https形式的访问站点
     */
    String getHttpsEndpoint();
}
