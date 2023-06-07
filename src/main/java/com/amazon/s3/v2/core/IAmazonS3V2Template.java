package com.amazon.s3.v2.core;

import com.amazon.s3.v2.config.S3V2Base;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.Part;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * @author liuyangfang
 * @description Amazon S3 V2版本对象存储服务的模板方法封装
 * @since 2023/6/1 16:52:16
 */
public interface IAmazonS3V2Template extends IAmazonS3V2Bucket, IAmazonS3V2Object {
    /**
     * 获取S3Client
     *
     * @return S3Client
     */
    S3Client getS3Client();

    /**
     * 获取基于异步构建的S3Client
     *
     * @return S3AsyncClient
     */
    S3AsyncClient getS3AsyncClient();


    /**
     * 获取基于S3AsyncClient构建的文件传输管理器
     *
     * @return S3AsyncClient构建的文件传输管理器
     */
    S3TransferManager getS3TransferManager();

    /**
     * 获取 S3Presigner
     *
     * @return S3Presigner
     */
    S3Presigner getS3Presigner();


    /**
     * 获取 S3Utilities
     *
     * @return S3Utilities
     */
    S3Utilities getS3Utilities();

    /**
     * 获取基础的配置信息
     *
     * @return S3V2Base
     */
    S3V2Base getS3V2Base();

    /**
     * 处理上传的对象名称
     * 对象上传的路径
     * 子类会提供一个默认的实现，当然你也可以自己实现
     * /baseDir/2023/01/01/uuid-objectName
     * <p>
     * 如果你希望你上传的文件命按照此规则进行存放的话，你可以调用此方法处理你的文件名称
     *
     * @param objectName 对象名称
     * @return 处理后的对象名称
     */
    String handlerUploadObjectName(String objectName);

    /**
     * 处理上传的对象名称
     * 对象上传的路径
     * 子类会提供一个默认的实现，当然你也可以自己实现
     * /baseDir/2023/01/01/uuid-objectName
     * <p>
     * 如果你希望你上传的文件命按照此规则进行存放的话，你可以调用此方法处理你的文件名称
     *
     * @param objectName 对象名称
     * @param baseDir    基础路径
     * @return 处理后的对象名称
     */
    String handlerUploadObjectName(String objectName, String baseDir);

    /**
     * 获取对象存储的前缀
     *
     * @return 对象前缀
     */
    String getUploadObjectNamePrefix();


    /**
     * 获取对象存储的前缀
     *
     * @param baseDir 基础路径
     * @return 对象前缀
     */
    String getUploadObjectNamePrefix(String baseDir);


    /**
     * 查询已经上传的所有的分片信息
     *
     * @param bucketName 桶的名称
     * @param objectName 对象名称
     * @param uploadId   上传ID
     */
    Optional<List<Part>> listParts(String bucketName, String objectName, String uploadId);


    /**
     * 获取预签名的对象的url
     *
     * @param bucketName    桶的名称
     * @param objectName    对象名称
     * @param signatureTime 签名url过期时间
     * @return 预签名的对象的url
     */
    Optional<PresignedGetObjectRequest> getPresignedUrl(String bucketName,
                                                        String objectName,
                                                        Duration signatureTime);


    /**
     * 获取预签名的上传URL
     *
     * @param bucketName    桶的名称
     * @param objectName    对象名称
     * @param contentType   文件类型
     * @param signatureTime 预签名put url的过期时间
     * @return 预签名的put url
     */
    Optional<PresignedPutObjectRequest> getPresignedPutUrl(String bucketName,
                                                           String objectName,
                                                           String contentType,
                                                           Duration signatureTime);

}
