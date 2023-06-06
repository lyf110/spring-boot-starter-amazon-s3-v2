package com.amazon.s3.v2.core.async;

import software.amazon.awssdk.transfer.s3.S3TransferManager;

/**
 * @author liuyangfang
 * @description
 * @since 2023/6/6 13:42:06
 */
public interface IAmazonS3V2AsyncObject {
    /**
     * 异步拷贝对象, 将对象从一个桶拷贝到另外一个桶
     *
     * @param srcBucketName  源对象所在的桶
     * @param srcObjectName  源对象名称
     * @param destBucketName 拷贝到的目标桶
     * @return etag
     */
    String asyncCopyObject(String srcBucketName,
                           String srcObjectName,
                           String destBucketName);

    /**
     * 异步拷贝对象
     *
     * @param srcBucketName  源对象所在的桶
     * @param srcObjectName  源对象名称
     * @param destBucketName 拷贝到的目标桶
     * @param destObjectName 拷贝到目标桶的对象名称
     * @return etag
     */
    String asyncCopyObject(String srcBucketName,
                           String srcObjectName,
                           String destBucketName,
                           String destObjectName);


    /**
     * 异步拷贝对象
     *
     * @param transferManager 异步文件传输管理器
     * @param srcBucketName   源对象所在的桶
     * @param srcObjectName   源对象名称
     * @param destBucketName  拷贝到的目标桶
     * @param destObjectName  拷贝到目标桶的对象名称
     * @return etag
     */
    String asyncCopyObject(S3TransferManager transferManager,
                           String srcBucketName,
                           String srcObjectName,
                           String destBucketName,
                           String destObjectName);


}
