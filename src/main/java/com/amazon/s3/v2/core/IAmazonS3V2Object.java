package com.amazon.s3.v2.core;

import com.amazon.s3.v2.core.async.IAmazonS3V2AsyncObject;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author liuyangfang
 * @description 提供Amazon S3 V2版本的Object操作方法接口
 * @since 2023/6/1 16:51:26
 */
public interface IAmazonS3V2Object extends IAmazonS3V2AsyncObject, IAmazonS3V2Upload, IAmazonS3V2Download {
    /**
     * 删除桶的方法，如果桶中存在对象，并且桶还设置了版本的话，那么会先删除桶中的对象还会删除桶的版本
     * 删除动作为逐个删除
     * 最后才真正删除桶对象
     *
     * @param bucketName 桶对象
     * @return 删除响应结果
     * @see IAmazonS3V2Object#deleteObjectsAndVersionsInBucketV2
     */
    @Deprecated
    Optional<DeleteBucketResponse> deleteObjectsAndVersionsInBucket(String bucketName);


    /**
     * 删除桶的方法，如果桶中存在对象，并且桶还设置了版本的话，那么会先删除桶中的对象还会删除桶的版本
     * 删除动作为批量删除
     * 最后才真正删除桶对象
     *
     * @param bucketName 桶对象
     * @return 删除响应结果
     */
    Optional<DeleteBucketResponse> deleteObjectsAndVersionsInBucketV2(String bucketName);

    /**
     * 删除桶中所有对象, 分批次请求，每批次又逐个删除
     *
     * @param bucketName 桶名称
     * @see IAmazonS3V2Object#deleteObjectsV2
     */
    @Deprecated
    void deleteObjects(String bucketName);

    /**
     * 删除桶中所有对象, 分批次请求，每批次使用deleteObjects() 删除节省网络开销
     *
     * @param bucketName 桶名称
     */
    void deleteObjectsV2(String bucketName);


    /**
     * 根据传入的ObjectIdentifierList进行删除多个对象
     *
     * @param bucketName           桶名称
     * @param objectIdentifierList 对象集合
     */
    Optional<DeleteObjectsResponse> deleteObjects(String bucketName, List<ObjectIdentifier> objectIdentifierList);


    /**
     * 删除所有的版本
     * 具体的删除动作使用的方法是deleteObject
     *
     * @param bucketName 桶名称
     * @see IAmazonS3V2Object#deleteVersionsV2
     */
    @Deprecated
    void deleteVersions(String bucketName);


    /**
     * 删除桶中的所有版本
     * 具体的删除逻辑使用的是deleteObjects
     *
     * @param bucketName 桶名称
     * @see S3Client#deleteObjects(DeleteObjectsRequest)
     */
    void deleteVersionsV2(String bucketName);

    /**
     * 对象拷贝的方法
     *
     * @param srcBucketName  源文件所在的桶
     * @param srcObjectName  源文件的对象名
     * @param destBucketName 需要拷贝到的目标桶
     * @param destObjectName 在目标桶的对象名
     * @return 拷贝结果
     */
    Optional<CopyObjectResponse> copyObject(String srcBucketName, String srcObjectName, String destBucketName, String destObjectName);


    /**
     * 对象拷贝的方法, 这里存储在目标桶的名称使用源文件的对象名称
     *
     * @param srcBucketName  源文件所在的桶
     * @param srcObjectName  源文件的对象名
     * @param destBucketName 需要拷贝到的目标桶
     * @return 拷贝结果
     */
    Optional<CopyObjectResponse> copyObject(String srcBucketName, String srcObjectName, String destBucketName);


    /**
     * 小文件上传方案 使用默认的桶进行存储
     *
     * @param multipartFile 前端上传的文件
     * @return 上传结果
     */
    Optional<PutObjectResponse> putObject(MultipartFile multipartFile) throws S3Exception, IOException;

    /**
     * 小文件上传方案
     *
     * @param bucketName    默认存储的桶名称
     * @param multipartFile 前端上传的文件
     * @return 上传结果
     */
    Optional<PutObjectResponse> putObject(String bucketName, MultipartFile multipartFile) throws S3Exception, IOException;


    /**
     * 小文件上传方案
     *
     * @param objectName    指定存储的对象名称
     * @param multipartFile 前端上传的文件
     * @return 上传结果
     */
    Optional<PutObjectResponse> putObject(MultipartFile multipartFile, String objectName) throws S3Exception, IOException;


    /**
     * 小文件上传方案
     *
     * @param bucketName    默认存储的桶名称
     * @param objectName    指定存储在桶中的名称
     * @param multipartFile 前端上传的文件
     * @return 上传结果
     */
    Optional<PutObjectResponse> putObject(String bucketName,
                                          String objectName,
                                          MultipartFile multipartFile) throws S3Exception, IOException;


    /**
     * 上传一个对象并使用默认的桶进行存储
     *
     * @param objectName    对象名称
     * @param contentType   对象类型
     * @param inputStream   文件输入流
     * @param contentLength 流长度
     * @return 返回结果
     * @throws S3Exception S3Exception
     */
    Optional<PutObjectResponse> putObject(
            String objectName,
            String contentType,
            InputStream inputStream,
            long contentLength) throws S3Exception, IOException;

    /**
     * 上传一个对象
     *
     * @param bucketName    桶名称
     * @param objectName    对象名称
     * @param contentType   对象类型
     * @param inputStream   文件输入流
     * @param contentLength 流长度
     * @return 返回结果
     * @throws S3Exception S3Exception
     */
    Optional<PutObjectResponse> putObject(
            String bucketName,
            String objectName,
            String contentType,
            InputStream inputStream,
            long contentLength) throws S3Exception, IOException;


    /**
     * 上传字符串到OSS
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param content    字符串对象
     * @return 返回结果
     * @throws S3Exception S3Exception
     */
    Optional<PutObjectResponse> putObject(
            String bucketName,
            String objectName,
            String content) throws S3Exception;

    /**
     * 上传字符串到OSS
     *
     * @param bucketName  桶名称
     * @param objectName  对象名称
     * @param content     字符串对象
     * @param metadataMap 元数据
     * @return 返回结果
     * @throws S3Exception S3Exception
     */
    Optional<PutObjectResponse> putObject(String bucketName,
                                          String objectName,
                                          String content,
                                          Map<String, String> metadataMap);


    /**
     * 上传一个对象
     *
     * @param bucketName  桶名称
     * @param objectName  对象名称
     * @param contentType 对象类型
     * @param metadataMap 元数据
     * @param requestBody 上传的对象
     * @return 返回结果
     * @throws S3Exception S3Exception
     */
    public Optional<PutObjectResponse> putObject(String bucketName,
                                                 String objectName,
                                                 String contentType,
                                                 Map<String, String> metadataMap,
                                                 RequestBody requestBody);


    /**
     * 上传一个对象
     *
     * @param bucketName  桶名称
     * @param objectName  对象名称
     * @param contentType 对象类型
     * @param requestBody 上传的对象
     * @return 返回结果
     * @throws S3Exception S3Exception
     */
    Optional<PutObjectResponse> putObject(
            String bucketName,
            String objectName,
            String contentType,
            RequestBody requestBody) throws S3Exception;

    /**
     * 查询桶的所有对象
     *
     * @param bucketName 桶对象
     * @return 桶中的所有对象
     */
    Optional<List<S3Object>> listObjects(String bucketName);

    /**
     * 查询指定桶指定目录下的所有对象
     *
     * @param bucketName 桶对象
     * @return 桶中的所有对象
     */
    Optional<List<S3Object>> listObjects(String bucketName, String objectPrefix);


    /**
     * 合并对象的方法
     *
     * @param originBucketName 源分块对象所在的桶
     * @param destBucketName   需要合并到的桶
     * @param destObjectName   需要合并对象的名称
     * @param s3Objects        需要合并的分片对象
     */
    Optional<CompleteMultipartUploadResponse> composeObject(String originBucketName,
                                                            String destBucketName,
                                                            String destObjectName,
                                                            List<S3Object> s3Objects);


    /**
     * 将指定桶中的所有对象合并到另外一个桶中
     *
     * @param originBucketName 源分块对象所在的桶
     * @param destBucketName   需要合并到的桶
     * @param destObjectName   需要合并对象的名称
     */
    Optional<CompleteMultipartUploadResponse> composeObject(String originBucketName,
                                                            String destBucketName,
                                                            String destObjectName);


    /**
     * 获取对象的输出流
     *
     * @param bucketName 对象所在的桶
     * @param objectName 对象在桶中的名称
     * @throws IOException IOException
     */
    Optional<ResponseInputStream<GetObjectResponse>> getObject(String bucketName, String objectName) throws IOException;


    /**
     * 获取对象的标准url
     *
     * @param bucketName 桶名
     * @param objectName 对象名
     * @return 对象的标准url
     */
    Optional<URL> getObjectUrl(String bucketName, String objectName);

}
