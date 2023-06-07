package com.amazon.s3.v2.auto.config;

import com.amazon.s3.v2.config.S3V2Base;
import com.amazon.s3.v2.template.AmazonS3V2Template;
import com.amazon.s3.v2.factory.AmazonS3V2Factory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * @author liuyangfang
 * @description
 * @since 2023/6/1 9:31:21
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {S3V2Base.class})
public class AmazonS3ClientV2AutoConfiguration {

    @Autowired
    private S3V2Base s3V2Base;

    @ConditionalOnMissingBean(AmazonS3V2Factory.class)
    @Bean(name = "amazonS3V2Factory")
    public AmazonS3V2Factory amazonS3V2Factory() {
        return new AmazonS3V2Factory();
    }

    @ConditionalOnMissingBean(AmazonS3V2Template.class)
    @Bean(name = "amazonS3V2Template")
    public AmazonS3V2Template amazonS3V2Template(AmazonS3V2Factory amazonS3V2Factory) throws MalformedURLException, URISyntaxException, NoSuchFieldException, IllegalAccessException {
        return amazonS3V2Factory.createAmazonS3V2Template(s3V2Base);
    }
}
