package com.amazon.s3.v2.core;

import com.amazon.s3.v2.core.async.IAmazonS3V2AsyncUpload;
import com.amazon.s3.v2.core.functions.MultipartUploadBiFunction;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author liuyangfang
 * @description 上传接口
 * @since 2023/6/6 11:47:37
 */
public interface IAmazonS3V2Upload extends IAmazonS3V2AsyncUpload {
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

}
