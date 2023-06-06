package com.amazon.s3.v2.core.async;

import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.UploadDirectoryRequest;

/**
 * @author liuyangfang
 * @description 异步上传接口
 * @since 2023/6/6 11:51:18
 */
public interface IAmazonS3V2AsyncUpload {
    /**
     * 上传目录
     *
     * @param sourceDirectory 需要上传的目录
     * @param bucketName      需要上传到的桶
     * @return 上传失败的文件数
     */
    Integer asyncUploadDirectory(String sourceDirectory, String bucketName);

    /**
     * 上传目录
     *
     * @param sourceDirectory     需要上传的目录
     * @param bucketName          需要上传到的桶
     * @param destDirectoryPrefix 在桶中存储的目录
     * @return 上传失败的文件数
     */
    Integer asyncUploadDirectory(String sourceDirectory, String bucketName, String destDirectoryPrefix);


    /**
     * 上传目录
     *
     * @param transferManager 基于S3AsyncClient构建的文件上传下载管理器
     * @param sourceDirectory 需要上传的目录
     * @param bucketName      需要上传到的桶
     * @return 上传失败的文件数
     */
    Integer asyncUploadDirectory(S3TransferManager transferManager,
                                 String sourceDirectory,
                                 String bucketName);


    /**
     * 上传目录
     *
     * @param transferManager     基于S3AsyncClient构建的文件上传下载管理器
     * @param sourceDirectory     需要上传的目录
     * @param bucketName          需要上传到的桶
     * @param destDirectoryPrefix 在桶中存储的目录
     * @return 上传失败的文件数
     */
    Integer asyncUploadDirectory(S3TransferManager transferManager,
                                 String sourceDirectory,
                                 String bucketName,
                                 String destDirectoryPrefix);


    /**
     * 上传目录
     *
     * @param uploadDirectoryRequest 目录上传参数构建
     * @return 上传失败的文件数
     */
    Integer asyncUploadDirectory(UploadDirectoryRequest uploadDirectoryRequest);

    /**
     * 上传目录
     *
     * @param transferManager        基于S3AsyncClient构建的文件上传下载管理器
     * @param uploadDirectoryRequest 目录上传参数构建
     * @return 上传失败的文件数
     */
    Integer asyncUploadDirectory(S3TransferManager transferManager,
                                 UploadDirectoryRequest uploadDirectoryRequest);


    /**
     * 异步上传单个文件
     *
     * @param objectName     文件上传到桶保存的名称
     * @param uploadFilePath 所需上传文件的路径
     * @return 上传成功后的eTag
     */
    String asyncUploadFile(String objectName,
                           String uploadFilePath);


    /**
     * 异步上传单个文件
     *
     * @param bucketName     文件上传到的桶名
     * @param objectName     文件上传到桶保存的名称
     * @param uploadFilePath 所需上传文件的路径
     * @return 上传成功后的eTag
     */
    String asyncUploadFile(String bucketName,
                           String objectName,
                           String uploadFilePath);


    /**
     * 异步上传单个文件
     *
     * @param transferManager 文件传输管理对象
     * @param objectName      文件上传到桶保存的名称
     * @param uploadFilePath  所需上传文件的路径
     * @return 上传成功后的eTag
     */
    String asyncUploadFile(S3TransferManager transferManager,
                           String objectName,
                           String uploadFilePath);


    /**
     * 异步上传单个文件
     *
     * @param transferManager 文件传输管理对象
     * @param bucketName      文件上传到的桶名
     * @param objectName      文件上传到桶保存的名称
     * @param uploadFilePath  所需上传文件的路径
     * @return 上传成功后的eTag
     */
    String asyncUploadFile(S3TransferManager transferManager,
                           String bucketName,
                           String objectName,
                           String uploadFilePath);
}
