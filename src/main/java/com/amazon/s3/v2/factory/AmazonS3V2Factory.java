package com.amazon.s3.v2.factory;

import com.amazon.s3.v2.config.S3V2Base;
import com.amazon.s3.v2.template.AmazonS3V2Template;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author liuyangfang
 * @description AmazonS3V2Template的工厂类
 * @since 2023/6/5 10:11:10
 */
public class AmazonS3V2Factory {
    /**
     * 创建Amazon S3 V2的标准客户端
     *
     * @param endPoint  oss服务器地址，或者是访问url
     * @param region    区域
     * @param accessKey 访问的凭证名
     * @param secretKey 访问的凭证密码
     * @return S3Client Amazon S3 V2的标准客户端
     * @throws URISyntaxException    URISyntaxException
     * @throws MalformedURLException MalformedURLException
     */
    public S3Client createS3Client(String endPoint, String region, String accessKey, String secretKey) throws URISyntaxException, MalformedURLException {
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
                .region(Region.of(region)) // 指定region
                .credentialsProvider(() -> credentials) // 提供认证凭证信息
                .endpointOverride(new URI(endPoint)) // 提供存储服务器的url
                .build();
    }

    /**
     * 创建Amazon S3 V2的预签名的客户端
     *
     * @param endPoint  oss服务器地址，或者是访问url
     * @param region    区域
     * @param accessKey 访问的凭证名
     * @param secretKey 访问的凭证密码
     * @return S3Presigner Amazon S3 V2的预签名的客户端
     * @throws URISyntaxException URISyntaxException
     */
    public S3Presigner createS3Presigner(String endPoint, String region, String accessKey, String secretKey) throws URISyntaxException {
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Presigner.builder()
                .region(Region.of(region)) // 指定region
                .credentialsProvider(() -> credentials) // 提供认证凭证信息
                .endpointOverride(new URI(endPoint)) // 提供存储服务器的url
                .build();
    }

    /**
     * 创建AmazonS3V2Template
     *
     * @param s3Client             s3Client
     * @param s3Presigner          s3Presigner
     * @param amazonS3V2Properties amazonS3V2Properties
     * @return AmazonS3V2Template
     */
    public AmazonS3V2Template createAmazonS3V2Template(
            S3Client s3Client, S3Presigner s3Presigner, S3V2Base amazonS3V2Properties) {
        return new AmazonS3V2Template(s3Client, s3Presigner, amazonS3V2Properties);
    }


    /**
     * 创建Amazon S3 V2的预签名的客户端
     *
     * @param endPoint   oss服务器地址，或者是访问url
     * @param region     区域
     * @param accessKey  访问的凭证名
     * @param secretKey  访问的凭证密码
     * @param bucketName 默认存储的桶的名称
     * @return AmazonS3V2Template
     */
    public AmazonS3V2Template createAmazonS3V2Template(String endPoint, String region, String accessKey, String secretKey, String bucketName) throws MalformedURLException, URISyntaxException {
        S3Client s3Client = createS3Client(endPoint, region, accessKey, secretKey);
        S3Presigner s3Presigner = createS3Presigner(endPoint, region, accessKey, secretKey);
        S3V2Base amazonS3V2Properties = S3V2Base.builder()
                .bucket(bucketName)
                .region(region)
                .accessKey(accessKey)
                .secretKey(secretKey)
                .endPoint(endPoint)
                .build();

        return createAmazonS3V2Template(s3Client, s3Presigner, amazonS3V2Properties);
    }


    /**
     * 创建Amazon S3 V2的预签名的客户端
     *
     * @param s3V2Base S3V2Base
     * @return AmazonS3V2Template
     */
    public AmazonS3V2Template createAmazonS3V2Template(S3V2Base s3V2Base) throws MalformedURLException, URISyntaxException {
        return createAmazonS3V2Template(s3V2Base.getEndPoint(), s3V2Base.getRegion(), s3V2Base.getAccessKey(), s3V2Base.getSecretKey(), s3V2Base.getBucket());
    }
}
