package com.amazon.s3.v2.core.async;

import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DownloadDirectoryRequest;

/**
 * @author liuyangfang
 * @description 异步下载接口
 * @since 2023/6/6 11:50:47
 */
public interface IAmazonS3V2AsyncDownload {

    /**
     * 目录下载
     *
     * @param transferManager          文件传输对象
     * @param downloadDirectoryRequest 目录下载请求
     * @return 下载失败的文件数量
     */
    Integer asyncDownloadDirectory(S3TransferManager transferManager,
                                   DownloadDirectoryRequest downloadDirectoryRequest);


    /**
     * 异步下载文件夹, 使用默认的存储桶
     *
     * @param saveDirectory 文件下载保存到本地的路径
     * @param objectPrefix  所需要下载的桶中的目录
     * @return 文件下载失败的个数，为0时表示全部下载成功
     */
    Integer asyncDownloadDirectory(String saveDirectory, String objectPrefix);

    /**
     * 异步下载文件夹
     *
     * @param bucketName    存储的桶名
     * @param saveDirectory 文件下载保存到本地的路径
     * @param objectPrefix  所需要下载的桶中的目录
     * @return 文件下载失败的个数，为0时表示全部下载成功
     */
    Integer asyncDownloadDirectory(String bucketName, String saveDirectory, String objectPrefix);


    /**
     * 异步下载文件夹, 使用默认的存储桶
     *
     * @param transferManager 文件传输管理对象
     * @param saveDirectory   文件下载保存到本地的路径
     * @param objectPrefix    所需要下载的桶中的目录
     * @return 文件下载失败的个数，为0时表示全部下载成功
     */
    Integer asyncDownloadDirectory(S3TransferManager transferManager,
                                   String saveDirectory,
                                   String objectPrefix);


    /**
     * 异步下载文件夹
     *
     * @param transferManager 文件传输管理对象
     * @param bucketName      存储的桶名
     * @param saveDirectory   文件下载保存到本地的路径
     * @param objectPrefix    所需要下载的桶中的目录
     * @return 文件下载失败的个数，为0时表示全部下载成功
     */
    Integer asyncDownloadDirectory(S3TransferManager transferManager,
                                   String bucketName,
                                   String saveDirectory,
                                   String objectPrefix);


    /**
     * 下载单个文件
     *
     * @param objectName 所需要下载的对象名
     * @param savePaths  文件下载保存到本地的路径
     * @return 下载的文件大小
     */
    Long asyncDownloadFile(String objectName, String savePaths);


    /**
     * 下载单个文件
     *
     * @param transferManager 文件传输管理器
     * @param objectName      所需要下载的对象名
     * @param savePaths       文件下载保存到本地的路径
     * @return 下载的文件大小
     */
    Long asyncDownloadFile(S3TransferManager transferManager, String objectName, String savePaths);


    /**
     * 下载单个文件
     *
     * @param bucketName 文件所在的桶名
     * @param objectName 所需要下载的对象名
     * @param savePaths  文件下载保存到本地的路径
     * @return 下载的文件大小
     */
    Long asyncDownloadFile(String bucketName, String objectName, String savePaths);


    /**
     * 下载单个文件
     *
     * @param transferManager 文件传输管理器
     * @param bucketName      文件所在的桶名
     * @param objectName      所需要下载的对象名
     * @param savePaths       文件下载保存到本地的路径
     * @return 下载的文件大小
     */
    Long asyncDownloadFile(S3TransferManager transferManager, String bucketName,
                           String objectName, String savePaths);


}
