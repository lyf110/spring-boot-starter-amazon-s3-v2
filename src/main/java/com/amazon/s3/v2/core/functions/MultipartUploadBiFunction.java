package com.amazon.s3.v2.core.functions;

/**
 * @author liuyangfang
 * @description
 * @since 2023/6/2 13:35:02
 */
@FunctionalInterface
public interface MultipartUploadBiFunction<T, R> {
    /**
     * Applies this function to the given arguments.
     *
     * @param targetObj     需要存储的对象
     * @param newBucketName 处理过后的桶名称
     * @param newObjectName 处理过后的对象名称
     * @param uploadId      此次分片上传的id
     * @return 函数的响应结果
     */
    R apply(T targetObj, String newBucketName, String newObjectName, String uploadId);
}
