package com.amazon.s3.v2.core;

import com.amazon.s3.v2.config.S3V2Base;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
     * 获取 S3Presigner
     *
     * @return S3Presigner
     */
    S3Presigner getS3Presigner();

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
     * 分片上传文件
     *
     * @param bucketName 对象桶
     * @param file       上传的文件对象
     */
    Optional<CompleteMultipartUploadResponse> multipartUpload(String bucketName, File file);


    /**
     * 分片上传文件
     *
     * @param bucketName 对象桶
     * @param file       上传的文件对象
     */
    Optional<CompleteMultipartUploadResponse> multipartUpload(String bucketName, String objectName, File file);


    /**
     * 分片上传文件
     *
     * @param bucketName 对象桶
     * @param objectName 指定文件的存储名称
     * @param file       上传的文件对象
     * @param sliceSize  分片大小
     */
    Optional<CompleteMultipartUploadResponse> multipartUpload(String bucketName, String objectName, File file, int sliceSize);


    /**
     * 分片上传文件
     *
     * @param bucketName 对象桶
     * @param file       上传的文件对象
     * @param sliceSize  分片大小
     */
    Optional<CompleteMultipartUploadResponse> multipartUpload(String bucketName, File file, int sliceSize);

    /**
     * 分片文件上传的底层封装方法
     *
     * @param bucketName 桶的名称
     * @param objectName 对象名称
     * @param t          这里是需要上传的对象
     * @param predicate  这里对上传的对象进行参数校验
     * @param function   这个函数是真正执行上传操作
     * @param <T>        上传对象的类型
     * @return CompleteMultipartUploadResponse
     * @throws S3Exception S3Exception
     */
    <T> Optional<CompleteMultipartUploadResponse> multipartUpload(String bucketName,
                                                                  String objectName,
                                                                  T t,
                                                                  Predicate<T> predicate,
                                                                  MultipartUploadBiFunction<T, List<CompletedPart>> function)
            throws S3Exception;

    /**
     * 上传分片文件, 不支持上传大文件RequestBody.fromBytes or fromByteBuffer 因为字节会驻留在内存中造成OOM
     * 这里最好的是上传切割后的单文件或者流文件
     *
     * @param bucketName      桶名称
     * @param objectName      存储到桶的名称（这个名称还是原始名称）
     * @param requestBodyList 分片对象
     * @throws S3Exception S3Exception
     */
    Optional<CompleteMultipartUploadResponse> multipartUpload(String bucketName,
                                                              String objectName,
                                                              List<RequestBody> requestBodyList)
            throws S3Exception;

    /**
     * 此操作中止多部分上传。多部分上传中止后，不能使用该上传ID上传其他部分。
     * 任何先前上传的部件消耗的存储都将被释放。
     * 但是，如果当前正在进行任何部分上传，则这些部分上传可能会成功，也可能不会成功。
     * 因此，可能有必要多次中止给定的多部分上传，以便完全释放所有部分消耗的所有存储。
     * 若要确认所有零件都已被移除，因此您无需收取零件存储费用，您应该调用ListParts 操作，并确保零件列表为空。
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param uploadId   上传ID
     */
    Optional<AbortMultipartUploadResponse> abortMultipartUpload(String bucketName,
                                                                String objectName,
                                                                String uploadId)
            throws S3Exception;


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
    Optional<PresignedGetObjectRequest> getPresignedUrl(String bucketName, String objectName, Duration signatureTime);
}
