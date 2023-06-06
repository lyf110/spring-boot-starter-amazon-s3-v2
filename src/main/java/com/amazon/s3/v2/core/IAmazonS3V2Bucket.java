package com.amazon.s3.v2.core;

import com.amazon.s3.v2.core.async.IAmazonS3V2AsyncBucket;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Optional;

/**
 * @author liuyangfang
 * @description 提供Amazon S3 V2版本的Bucket操作方法接口
 * @since 2023/6/1 16:50:45
 */
public interface IAmazonS3V2Bucket extends IAmazonS3V2AsyncBucket {

    /**
     * 获取设置的默认桶
     *
     * @return 默认的桶
     */
    String getDefaultBucket();

    /**
     * 判断Bucket是否存在
     *
     * @param bucketName 桶名称
     */
    boolean isBucketExists(String bucketName) throws S3Exception;


    /**
     * 创建一个桶对象
     *
     * @param bucketName 存储的桶名
     *                   需要注意的是腾讯云的bucket命名是按照如下格式的
     *                   《bucket-appleId》ex: my-test-bucket-12321321123
     * @return 桶对象
     */
    Optional<CreateBucketResponse> createBucket(String bucketName);

    /**
     * 根据桶的名称获取桶对象
     *
     * @param bucketName bucketName
     * @return Bucket
     */
    Optional<Bucket> getBucketByName(String bucketName);


    /**
     * 获取所有的桶
     *
     * @return 所有的桶
     */
    Optional<ListBucketsResponse> listBucket();

    /**
     * 获取所有的桶按条件过滤
     *
     * @param listBucketsRequest listBucketsRequest
     * @return 所有的桶
     */
    Optional<ListBucketsResponse> listBucket(ListBucketsRequest listBucketsRequest);
}
