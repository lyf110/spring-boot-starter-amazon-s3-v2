package com.amazon.s3.v2.core;

import com.amazon.s3.v2.core.async.IAmazonS3V2AsyncDownload;

import java.io.IOException;

/**
 * @author liuyangfang
 * @description 下载接口
 * @since 2023/6/6 11:48:16
 */
public interface IAmazonS3V2Download extends IAmazonS3V2AsyncDownload {
    /**
     * 下载一个文件
     *
     * @param bucketName       文件所在的桶
     * @param objectName       对象名
     * @param downloadBasePath 下载到本地所在的目录
     * @throws IOException IOException
     */
    void downloadFile(String bucketName, String objectName, String downloadBasePath) throws IOException;

    /**
     * 下载文件夹
     *
     * @param bucketName       桶名称
     * @param objectPrefix     对象前缀，也可以理解为文件夹
     * @param downloadBasePath 下载到本地的基础路径
     * @throws IOException IOException
     */
    void downloadDirectory(String bucketName, String objectPrefix, String downloadBasePath) throws IOException;

}
