package com.amazon.s3.v2.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.amazon.s3.v2.config.S3V2Base;
import com.amazon.s3.v2.constant.BusinessV2Constant;
import com.amazon.s3.v2.core.IAmazonS3V2Template;
import com.amazon.s3.v2.core.MultipartUploadBiFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.HttpStatusFamily;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.amazon.s3.v2.constant.BusinessV2Constant.*;

/**
 * @author liuyangfang
 * @description
 * @since 2023/6/1 10:49:15
 */
@Slf4j
public class AmazonS3V2Template implements IAmazonS3V2Template {
    /**
     * 默认的分片大小5M
     */
    private static final int DEFAULT_SLICE_SIZE = 5 * 1024 * 1024;
    public static final long MIN_UPLOAD_SIZE = 0L;
    public static final int MAX_SINGLETON_SIZE = (int) (0.8 * MAX_UPLOAD_SIZE);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3V2Base s3V2Base;


    public AmazonS3V2Template(S3Client s3Client, S3Presigner s3Presigner, S3V2Base s3V2Base) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.s3V2Base = s3V2Base;
    }

    @Override
    public String getDefaultBucket() {
        return s3V2Base.getBucket();
    }

    /**
     * 判断Bucket是否存在
     *
     * @param bucketName 桶名称
     */
    @Override
    public boolean isBucketExists(String bucketName) throws S3Exception {
        // 处理桶的名称
        bucketName = handlerBucketName(bucketName);

        try {
            HeadBucketResponse headBucketResponse = s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            return headBucketResponse.sdkHttpResponse().isSuccessful();
        } catch (S3Exception e) {
            int code = e.toBuilder().statusCode();
            if (HttpStatusFamily.CLIENT_ERROR == HttpStatusFamily.of(code)) {
                log.error("{}", e.getMessage());
                return false;
            }
            throw e;
        }
    }

    @Override
    public Optional<PutObjectResponse> putObject(String bucketName, MultipartFile multipartFile) throws S3Exception, IOException {
        Assert.notNull(multipartFile, "multipartFile not null");
        return putObject(bucketName, multipartFile.getOriginalFilename(), multipartFile);
    }

    @Override
    public Optional<PutObjectResponse> putObject(MultipartFile multipartFile, String objectName) throws S3Exception, IOException {
        return putObject(getDefaultBucket(), objectName, multipartFile);
    }

    @Override
    public Optional<PutObjectResponse> putObject(String bucketName, String objectName, MultipartFile multipartFile) throws S3Exception, IOException {
        Assert.notNull(multipartFile, "multipartFile not null");
        return putObject(bucketName,
                objectName,
                multipartFile.getContentType(),
                multipartFile.getInputStream(),
                multipartFile.getSize());
    }


    @Override
    public Optional<PutObjectResponse> putObject(String objectName, String contentType, InputStream inputStream, long contentLength) throws S3Exception, IOException {
        return putObject(getDefaultBucket(), objectName, contentType, inputStream, contentLength);
    }

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
    public Optional<PutObjectResponse> putObject(String bucketName,
                                                 String objectName,
                                                 String contentType,
                                                 InputStream inputStream,
                                                 long contentLength) throws S3Exception, IOException {

        Assert.notNull(inputStream, "inputStream not empty");
        Assert.checkBetween(contentLength, MIN_UPLOAD_SIZE, MAX_UPLOAD_SIZE);

        // 这里需要增加关流的操作，PutObject方法是不会自动关流的
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            return putObject(bucketName, objectName, contentType, RequestBody.fromInputStream(bufferedInputStream, contentLength));
        }
    }


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
    @Override
    public Optional<PutObjectResponse> putObject(String bucketName,
                                                 String objectName,
                                                 String contentType,
                                                 RequestBody requestBody) throws S3Exception, IOException {
        bucketName = handlerBucketName(bucketName);
        Assert.notEmpty(objectName, "object name not empty");
        Assert.notNull(requestBody, "requestBody not empty");


        // 这里需要增加关流的操作，PutObject方法是不会自动关流的

        PutObjectRequest.Builder builder = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName);

        if (StrUtil.isNotEmpty(contentType)) {
            builder.contentType(contentType);
        }


        PutObjectResponse putObjectResponse = s3Client.putObject(builder.build(), requestBody);
        return Optional.of(putObjectResponse);
    }

    /**
     * 创建一个桶对象, 针对腾讯云的适配，腾讯云需要传入AppId
     *
     * @param bucketName 存储的桶名
     *                   需要注意的是腾讯云的bucket命名是按照如下格式的
     *                   《bucket-appleId》ex: my-test-bucket-12321321123
     * @return 桶对象
     */
    @Override
    public Optional<CreateBucketResponse> createBucket(String bucketName) {
        // 处理桶的名称
        bucketName = handlerBucketName(bucketName);

        if (isBucketExists(bucketName)) {
            log.info("the bucket {} already exists", bucketName);
            return Optional.of(CreateBucketResponse.builder().location(FILE_SEPARATOR + bucketName).build());
        }

        try {
            CreateBucketResponse createBucketResponse = s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            log.info("create bucket {} success", bucketName);
            return Optional.ofNullable(createBucketResponse);
        } catch (S3Exception e) {
            log.error("create bucket {} failed, the cause is ", bucketName, e);
            return Optional.empty();
        }
    }

    /**
     * 根据桶的名称获取桶对象
     *
     * @param bucketName bucketName
     * @return Bucket
     */
    @Override
    public Optional<Bucket> getBucketByName(String bucketName) {
        // 处理桶的名称
        bucketName = handlerBucketName(bucketName);

        boolean present = listBucket().isPresent();
        if (present) {
            ListBucketsResponse listBucketsResponse = listBucket().get();
            List<Bucket> buckets = listBucketsResponse.buckets();
            if (CollectionUtil.isNotEmpty(buckets)) {
                for (Bucket bucket : buckets) {
                    if (bucket.name().equalsIgnoreCase(bucketName)) {
                        return Optional.of(bucket);
                    }
                }
            }
        }

        return Optional.empty();
    }


    /**
     * 获取所有的桶
     *
     * @return 所有的桶
     */
    @Override
    public Optional<ListBucketsResponse> listBucket() {
        try {
            ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
            return Optional.ofNullable(listBucketsResponse);
        } catch (S3Exception e) {
            log.error("get bucket list failed, the cause is ", e);
            return Optional.empty();
        }
    }


    /**
     * 获取所有的桶
     *
     * @param listBucketsRequest listBucketsRequest
     * @return 所有的桶
     */
    @Override
    public Optional<ListBucketsResponse> listBucket(ListBucketsRequest listBucketsRequest) {
        try {
            ListBucketsResponse listBucketsResponse = s3Client.listBuckets(listBucketsRequest);
            return Optional.ofNullable(listBucketsResponse);
        } catch (S3Exception e) {
            log.error("get bucket list failed, the cause is ", e);
            return Optional.empty();
        }
    }


    /**
     * 删除桶的方法，如果桶中存在对象，并且桶还设置了版本的话，那么会先删除桶中的对象还会删除桶的版本
     * 删除动作为逐个删除
     * 最后才真正删除桶对象
     *
     * @param bucketName 桶对象
     * @return 删除响应结果
     * @see AmazonS3V2Template#deleteObjectsAndVersionsInBucketV2(String)
     */
    @Deprecated
    @Override
    public Optional<DeleteBucketResponse> deleteObjectsAndVersionsInBucket(String bucketName) {
        bucketName = handlerBucketName(bucketName);
        try {
            // 删除所有的对象
            deleteObjects(bucketName);

            // 如果是多版本对象的话，我们还需要删除桶对象的不同版本
            deleteVersions(bucketName);

            // 删除完所有的对象之后，我们才删除桶
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
            DeleteBucketResponse deleteBucketResponse = s3Client.deleteBucket(deleteBucketRequest);
            log.info("delete bucket {} success", bucketName);
            return Optional.ofNullable(deleteBucketResponse);
        } catch (S3Exception e) {
            log.error("delete bucket {} failed, the cause is ", bucketName, e);
            return Optional.empty();
        }
    }


    /**
     * 删除桶的方法，如果桶中存在对象，并且桶还设置了版本的话，那么会先删除桶中的对象还会删除桶的版本
     * 删除动作为批量删除
     * 最后才真正删除桶对象
     *
     * @param bucketName 桶对象
     * @return 删除响应结果
     */
    @Override
    public Optional<DeleteBucketResponse> deleteObjectsAndVersionsInBucketV2(String bucketName) {
        bucketName = handlerBucketName(bucketName);

        try {
            // 要删除桶，我们需要先删除桶中的对象先
            deleteObjectsV2(bucketName);

            // 如果是多版本对象的话，我们还需要删除桶对象的不同版本
            deleteVersionsV2(bucketName);

            // 删除完所有的对象之后，我们才删除桶
            DeleteBucketResponse deleteBucketResponse = s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
            log.info("delete bucket {} success", bucketName);
            return Optional.ofNullable(deleteBucketResponse);
        } catch (S3Exception e) {
            log.error("delete bucket {} failed, the cause is ", bucketName, e);
            return Optional.empty();
        }
    }

    /**
     * 删除桶中所有对象, 分批次请求，每批次又逐个删除
     *
     * @param bucketName 桶名称
     * @see AmazonS3V2Template#deleteObjectsV2(String)
     */
    @Deprecated
    @Override
    public void deleteObjects(String bucketName) {
        bucketName = handlerBucketName(bucketName);
        try {
            ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();
            ListObjectsV2Response listObjectsV2Response;

            // 每次删除一部分，分批删除
            do {
                listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
                for (S3Object s3Object : listObjectsV2Response.contents()) {
                    DeleteObjectRequest request = DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Object.key())
                            .build();
                    s3Client.deleteObject(request);
                }
            } while (listObjectsV2Response.isTruncated());
        } catch (S3Exception e) {
            log.error("delete bucket {} objects failed, the cause is ", bucketName, e);
        }
    }

    /**
     * 删除桶中所有对象, 分批次请求，每批次使用deleteObjects() 删除节省网络开销
     *
     * @param bucketName 桶名称
     */
    @Override
    public void deleteObjectsV2(String bucketName) {
        bucketName = handlerBucketName(bucketName);
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();
        ListObjectsV2Response listObjectsV2Response;

        try {
            // 每次删除一部分，分批删除
            do {
                listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
                deleteObjects(bucketName, listObjectsV2Response.contents()
                        .stream()
                        .map(s3Object -> ObjectIdentifier.builder().key(s3Object.key()).build())
                        .collect(Collectors.toList()));
            } while (listObjectsV2Response.isTruncated());
        } catch (S3Exception e) {
            log.error("delete bucket {} objects failed, the cause is ", bucketName, e);
        }
    }

    /**
     * 根据传入的ObjectIdentifierList进行删除多个对象
     *
     * @param bucketName           桶名称
     * @param objectIdentifierList 对象集合
     */
    @Override
    public Optional<DeleteObjectsResponse> deleteObjects(String bucketName, List<ObjectIdentifier> objectIdentifierList) {
        // 将桶的名称转成全小写
        bucketName = handlerBucketName(bucketName);
        if (CollectionUtil.isEmpty(objectIdentifierList)) {
            log.warn("bucket {} , the objectIdentifierList is empty, not need delete", bucketName);
            return Optional.empty();
        }


        try {
            // 执行全量删除的操作
            DeleteObjectsResponse deleteObjectsResponse = s3Client.deleteObjects(DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder().objects(objectIdentifierList).build())
                    .build());

            return Optional.ofNullable(deleteObjectsResponse);
        } catch (AwsServiceException | SdkClientException e) {
            log.error("delete bucket {} Objects size {} failed, the cause is ", bucketName, objectIdentifierList.size(), e);
            return Optional.empty();
        }
    }


    /**
     * 删除所有的版本
     * 具体的删除动作使用的方法是deleteObject
     *
     * @param bucketName 桶名称
     * @see S3Client#deleteObject(DeleteObjectRequest)
     */
    @Deprecated
    @Override
    public void deleteVersions(String bucketName) {
        ListObjectVersionsRequest listObjectVersionsRequest = ListObjectVersionsRequest.builder().bucket(bucketName).build();
        ListObjectVersionsResponse listObjectVersionsResponse;
        do {
            listObjectVersionsResponse = s3Client.listObjectVersions(listObjectVersionsRequest);
            // 如果版本不为空的话，那么就执行删除操作
            List<ObjectVersion> versionList = listObjectVersionsResponse.versions();
            if (CollectionUtil.isNotEmpty(versionList)) {
                for (ObjectVersion version : versionList) {
                    s3Client.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(version.key())
                            .versionId(version.versionId())
                            .build());
                }
            }

            // 可能存在listObjectVersions为空但是deleteMarkers 不为空的情况
            List<DeleteMarkerEntry> deleteMarkerEntryList = listObjectVersionsResponse.deleteMarkers();
            if (CollectionUtil.isNotEmpty(deleteMarkerEntryList)) {
                for (DeleteMarkerEntry deleteMarkerEntry : deleteMarkerEntryList) {
                    s3Client.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(deleteMarkerEntry.key())
                            .versionId(deleteMarkerEntry.versionId())
                            .build());
                }
            }
        } while (listObjectVersionsResponse.isTruncated());
    }


    /**
     * 删除桶中的所有版本
     * 具体的删除逻辑使用的是deleteObjects
     *
     * @param bucketName 桶名称
     * @see S3Client#deleteObjects(DeleteObjectsRequest)
     */
    @Override
    public void deleteVersionsV2(String bucketName) {
        ListObjectVersionsRequest listObjectVersionsRequest = ListObjectVersionsRequest.builder().bucket(bucketName).build();
        ListObjectVersionsResponse listObjectVersionsResponse;
        do {
            listObjectVersionsResponse = s3Client.listObjectVersions(listObjectVersionsRequest);
            // 如果版本不为空的话，那么就执行删除操作
            deleteObjects(bucketName, getObjectIdentifierListByVersionList(listObjectVersionsResponse.versions()));

            // 可能存在listObjectVersions为空但是deleteMarkers 不为空的情况
            deleteObjects(bucketName, getObjectIdentifierListByDeleteMarkers(listObjectVersionsResponse.deleteMarkers()));
        } while (listObjectVersionsResponse.isTruncated());
    }


    /**
     * 根据版本集合封装deleteObjects需要的对象集
     *
     * @param versionList 版本集合
     * @return deleteObjects需要的对象集
     */
    private List<ObjectIdentifier> getObjectIdentifierListByVersionList(List<ObjectVersion> versionList) {
        if (CollectionUtil.isEmpty(versionList)) {
            return Collections.emptyList();
        }

        return versionList.stream()
                .map(objectVersion ->
                        ObjectIdentifier.builder()
                                .key(objectVersion.key())
                                .versionId(objectVersion.versionId())
                                .build())
                .collect(Collectors.toList());
    }


    /**
     * 根据DeleteMarkerEntry集合封装deleteObjects需要的对象集
     *
     * @param deleteMarkerEntryList DeleteMarkerEntry集合
     * @return deleteObjects需要的对象集
     */
    private List<ObjectIdentifier> getObjectIdentifierListByDeleteMarkers(List<DeleteMarkerEntry> deleteMarkerEntryList) {
        if (CollectionUtil.isEmpty(deleteMarkerEntryList)) {
            return Collections.emptyList();
        }

        return deleteMarkerEntryList.stream()
                .map(objectVersion ->
                        ObjectIdentifier.builder()
                                .key(objectVersion.key())
                                .versionId(objectVersion.versionId())
                                .build())
                .collect(Collectors.toList());
    }


    /**
     * 处理桶名称，因为Amazon S3 不支持大写字母的，所以需要将桶名称转成小写
     *
     * @param bucketName 桶名称
     * @return 转成小写后的桶名称
     */
    private String handlerBucketName(String bucketName) {
        // 非空校验
        Assert.notEmpty(bucketName, "bucket name is not empty");

        // 返回处理过的桶名称
        return bucketName.toLowerCase(Locale.ENGLISH);
    }


    @Override
    public S3Client getS3Client() {
        return s3Client;
    }

    @Override
    public S3Presigner getS3Presigner() {
        return s3Presigner;
    }

    @Override
    public S3V2Base getS3V2Base() {
        return s3V2Base;
    }

    @Override
    public String handlerUploadObjectName(String objectName) {
        return handlerUploadObjectName(objectName, DEFAULT_UPLOAD_BASE_DIR);
    }

    /**
     * 处理上传的对象名称
     * 对象上传的路径
     * 子类会提供一个默认的实现，当然你也可以自己实现
     * /{baseDir}/2023/01/01/{uuid}-{objectName}
     * <p>
     * 如果你希望你上传的文件命按照此规则进行存放的话，你可以调用此方法处理你的文件名称
     *
     * @param objectName 对象名称
     * @param baseDir    基础路径
     * @return 处理后的对象名称
     */
    @Override
    public String handlerUploadObjectName(String objectName, String baseDir) {
        if (StrUtil.isEmpty(objectName)) {
            return objectName;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getUploadObjectNamePrefix(baseDir));
        stringBuilder.append(FILENAME_LINK);
        stringBuilder.append(objectName);
        return stringBuilder.toString();
    }

    @Override
    public String getUploadObjectNamePrefix() {
        return getUploadObjectNamePrefix(DEFAULT_UPLOAD_BASE_DIR);
    }

    @Override
    public String getUploadObjectNamePrefix(String baseDir) {
        StringBuilder stringBuilder = new StringBuilder();
        if (StrUtil.isEmpty(baseDir)) {
            baseDir = DEFAULT_UPLOAD_BASE_DIR;
        }

        stringBuilder.append(baseDir);
        stringBuilder.append(FILE_SEPARATOR);
        stringBuilder.append(LocalDateTime.now().format(FILE_NAME_PATTERN));
        stringBuilder.append(FILE_SEPARATOR);
        stringBuilder.append(UUID.randomUUID().toString(true));
        return stringBuilder.toString();
    }

    @Override
    public Optional<CompleteMultipartUploadResponse> multipartUpload(String bucketName, File file) {
        return multipartUpload(bucketName, file, DEFAULT_SLICE_SIZE);
    }

    @Override
    public Optional<CompleteMultipartUploadResponse> multipartUpload(String bucketName, String objectName, File file) {
        return multipartUpload(bucketName, objectName, file, DEFAULT_SLICE_SIZE);
    }


    /**
     * 对象拷贝的方法
     *
     * @param srcBucketName  源文件所在的桶
     * @param srcObjectName  源文件的对象名
     * @param destBucketName 需要拷贝到的目标桶
     * @param destObjectName 在目标桶的对象名
     * @return 拷贝结果
     */
    @Override
    public Optional<CopyObjectResponse> copyObject(String srcBucketName, String srcObjectName, String destBucketName, String destObjectName) {
        Assert.notEmpty(srcBucketName, "srcBucketName not empty");
        Assert.notEmpty(srcObjectName, "srcObjectName not empty");
        Assert.notEmpty(destBucketName, "destBucketName not empty");
        Assert.notEmpty(destObjectName, "destObjectName not empty");

        try {
            return Optional.ofNullable(s3Client.copyObject(CopyObjectRequest.builder()
                    .sourceBucket(srcBucketName)
                    .sourceKey(srcObjectName)
                    .destinationBucket(destBucketName)
                    .destinationKey(destObjectName)
                    .build()));
        } catch (S3Exception e) {
            log.error("srcBucketName {} srcObjectName {} destBucketName {} destObjectName {}, copy object failed, the cause is ",
                    srcBucketName,
                    srcObjectName,
                    destBucketName,
                    destObjectName,
                    e
            );

            return Optional.empty();
        }
    }

    /**
     * 对象拷贝的方法, 这里存储在目标桶的名称使用源文件的对象名称
     *
     * @param srcBucketName  源文件所在的桶
     * @param srcObjectName  源文件的对象名
     * @param destBucketName 需要拷贝到的目标桶
     * @return 拷贝结果
     */
    @Override
    public Optional<CopyObjectResponse> copyObject(String srcBucketName, String srcObjectName, String destBucketName) {
        return copyObject(srcBucketName, srcObjectName, destBucketName, srcObjectName);
    }

    @Override
    public Optional<PutObjectResponse> putObject(MultipartFile multipartFile) throws S3Exception, IOException {
        return putObject(getDefaultBucket(), multipartFile);
    }


    /**
     * 分片上传文件
     *
     * @param bucketName 对象桶
     * @param objectName 指定文件的存储名称
     * @param file       上传的文件对象
     * @param sliceSize  分片大小
     */
    @Override
    public Optional<CompleteMultipartUploadResponse> multipartUpload(String bucketName, String objectName, File file, int sliceSize) {
        return multipartUpload(bucketName,
                objectName,
                file,
                file1 -> file1 != null && file1.exists(),
                (file2, newBucketName, newObjectName, uploadId) -> {
                    // 文件大小
                    long contentLength = file.length();
                    // 计算分片数量
                    int sliceParts = (int) Math.ceil(contentLength * 1.0d / sliceSize);

                    List<CompletedPart> completedPartList = new ArrayList<>(sliceParts);
                    try (RandomAccessFile randomAccessFileRead = new RandomAccessFile(file, "r")) {
                        long filePosition = 0;
                        long partSize = sliceSize;


                        // 上传分片
                        for (int partNumber = 1; filePosition < contentLength; partNumber++) {
                            // 由于最后一个分片的大小可能会小于5M, 所以需要动态调整分片大小
                            partSize = Math.min(partSize, (contentLength - filePosition));

                            // 随机读取
                            randomAccessFileRead.seek(filePosition);
                            byte[] bytes = new byte[(int) partSize];
                            randomAccessFileRead.readFully(bytes);

                            // 创建一个分片的上传请求
                            UploadPartRequest uploadRequest = UploadPartRequest.builder()
                                    .bucket(newBucketName)
                                    .key(newObjectName)
                                    .uploadId(uploadId)
                                    .partNumber(partNumber).build();

                            String etag = s3Client.uploadPart(uploadRequest, RequestBody.fromBytes(bytes)).eTag();
                            log.info("part {}, upload success", partNumber);

                            CompletedPart part = CompletedPart.builder().partNumber(partNumber).eTag(etag).build();
                            completedPartList.add(part);

                            filePosition += partSize;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return completedPartList;
                }
        );
    }

    /**
     * 分片上传文件
     *
     * @param bucketName 对象桶
     * @param file       上传的文件对象
     * @param sliceSize  分片大小
     */
    @Override
    public Optional<CompleteMultipartUploadResponse> multipartUpload(String bucketName, File file, int sliceSize) {
        Assert.notNull(file, "upload file not null");
        return multipartUpload(bucketName, file.getName(), file, sliceSize);
    }

    /**
     * 分片文件上传的底层封装方法
     *
     * @param bucketName 桶的名称
     * @param objectName 对象名称
     * @param t          这里是需要上传的对象
     * @param predicate  这里对上传的对象进行参数校验
     * @param function   这个函数是真正执行上传操作
     * @param <T>        上传对象的类型
     * @throws S3Exception S3Exception
     */
    @Override
    public <T> Optional<CompleteMultipartUploadResponse> multipartUpload(String bucketName,
                                                                         String objectName,
                                                                         T t,
                                                                         Predicate<T> predicate,
                                                                         MultipartUploadBiFunction<T, List<CompletedPart>> function)
            throws S3Exception {
        // 处理桶名称和对象名称
        bucketName = handlerBucketName(bucketName);

        // 校验对象名称
        Assert.notEmpty(objectName, "objectName not empty");

        // 参数校验
        boolean paramCheckResult = predicate.test(t);
        if (!paramCheckResult) {
            // 校验不通过
            throw new IllegalArgumentException("param check failed, " + t.toString());
        }

        // 首先创建一个分片上传，并获取上传id
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build();

        // 获取分片上传的对象
        CreateMultipartUploadResponse response = s3Client.createMultipartUpload(createMultipartUploadRequest);

        // 获取上传ID
        String uploadId = response.uploadId();
        log.info("bucket {} object name {}, uploadId {}", bucketName, objectName, uploadId);


        try {
            // 获取上传分片对象及
            List<CompletedPart> completedPartList = function.apply(t, bucketName, objectName, uploadId);

            // 最后调用completeMultipartUpload操作告诉S3合并所有上传的部分并完成多部分操作。
            CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                    .parts(completedPartList)
                    .build();

            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    CompleteMultipartUploadRequest.builder()
                            .bucket(bucketName)
                            .key(objectName)
                            .uploadId(uploadId)
                            .multipartUpload(completedMultipartUpload)
                            .build();
            // 最后完成分片上传逻辑
            CompleteMultipartUploadResponse completeMultipartUploadResponse = s3Client.completeMultipartUpload(completeMultipartUploadRequest);
            log.info("Upload an object in parts success, bucket {} object name {}, uploadId {}",
                    bucketName,
                    objectName,
                    uploadId);
            return Optional.of(completeMultipartUploadResponse);
        } catch (Exception e) {
            log.error("Upload an object in parts failed, bucket {} object name {}, uploadId {}, the cause is ", bucketName, objectName, uploadId, e);
            // 这里需要取消已经上传的分片
            try {
                Optional<AbortMultipartUploadResponse> abortMultipartUploadResponse = abortMultipartUpload(bucketName, objectName, uploadId);
                if (abortMultipartUploadResponse.isPresent()) {
                    log.info("abort upload objects part success, bucket {} object name {}, uploadId {}", bucketName, objectName, uploadId);
                }
            } catch (AwsServiceException | SdkClientException ex) {
                throw new RuntimeException(ex);
            }
        }
        return Optional.empty();
    }


    /**
     * 上传分片文件
     * 上传分片文件, 不支持上传大文件RequestBody.fromBytes or fromByteBuffer 因为字节会驻留在内存中造成OOM
     * 这里最好的是上传切割后的单文件或者流文件
     *
     * @param bucketName      桶名称
     * @param objectName      存储到桶的名称（这个名称还是原始名称）
     * @param requestBodyList 分片对象
     * @throws S3Exception S3Exception
     */
    @Override
    public Optional<CompleteMultipartUploadResponse> multipartUpload(String bucketName,
                                                                     String objectName,
                                                                     List<RequestBody> requestBodyList) throws S3Exception {
        return multipartUpload(bucketName, objectName, requestBodyList, this::checkRequestBodyList,
                (requestBodies, handlerBucketName, handlerObjectName, uploadId) -> {

                    // Upload the file parts.
                    // 这里的partNumber是从1开始的
                    List<CompletedPart> completedPartList = new ArrayList<>(requestBodies.size());
                    int partNumber;
                    for (int index = 0; index < requestBodies.size(); index++) {
                        partNumber = index + 1;
                        // 创建一个分片的上传请求
                        UploadPartRequest uploadRequest = UploadPartRequest.builder()
                                .bucket(handlerBucketName)
                                .key(handlerObjectName)
                                .uploadId(uploadId)
                                .partNumber(partNumber).build();

                        String etag = s3Client.uploadPart(uploadRequest, requestBodies.get(index)).eTag();
                        log.info("part {}, upload success", partNumber);

                        CompletedPart part = CompletedPart.builder().partNumber(partNumber).eTag(etag).build();
                        completedPartList.add(part);
                    }
                    return completedPartList;
                }
        );
    }

    /**
     * 确保除了最后一个分片之外，每个分片的大小都最少为5MB
     *
     * @param requestBodyList 分片集合
     * @return 校验分片
     */
    private boolean checkRequestBodyList(List<RequestBody> requestBodyList) {
        // 对RequestBody进行非空判断
        if (CollectionUtil.isEmpty(requestBodyList)) {
            log.error("part list not be empty");
            return false;
        }

        // Amazon S3 规定上传的分片数量必须在1到10000之间，超过会报错Part number must be an integer between 1 and 10000
        int size = requestBodyList.size();
        if (size < 1 || size > 10000) {
            log.error("Part number must be an integer between 1 and 10000");
            return false;
        }

        // 获取每一个分片的大小
        for (int i = 0; i < size; i++) {
            Optional<Long> longOptional = requestBodyList.get(i).optionalContentLength();
            if (!longOptional.isPresent()) {
                return false;
            }
            Long partLength = longOptional.get();
            if (i != size - 1) {
                if (partLength < DEFAULT_SLICE_SIZE) {
                    log.error("Your proposed upload is smaller than the minimum allowed object size 5MB");
                    return false;
                }
            }
        }
        return true;
    }

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
    @Override
    public Optional<AbortMultipartUploadResponse> abortMultipartUpload(String bucketName,
                                                                       String objectName,
                                                                       String uploadId)
            throws S3Exception {
        bucketName = handlerBucketName(bucketName);
        Assert.notEmpty(objectName, "object name is not empty");
        Assert.notEmpty(uploadId, "uploadId is not empty");

        AbortMultipartUploadRequest abortMultipartUploadRequest = AbortMultipartUploadRequest.builder()
                .bucket(bucketName)
                .uploadId(uploadId)
                .key(objectName)
                .build();


        AbortMultipartUploadResponse abortMultipartUploadResponse;

        Optional<List<Part>> optionalPartList;

        try {
            abortMultipartUploadResponse = s3Client.abortMultipartUpload(abortMultipartUploadRequest);
            while (true) {
                optionalPartList = listParts(bucketName, objectName, uploadId);
                if (!optionalPartList.isPresent()) {
                    break;
                }

                if (CollectionUtil.isEmpty(optionalPartList.get())) {
                    break;
                }
                abortMultipartUploadResponse = s3Client.abortMultipartUpload(abortMultipartUploadRequest);
            }

            return Optional.ofNullable(abortMultipartUploadResponse);
        } catch (AwsServiceException | SdkClientException e) {
            log.error("abortMultipartUpload failed, bucket {}, objectName {}, uploadId {}, the cause is ",
                    bucketName, objectName, uploadId, e);
            throw e;
        }
    }

    /**
     * 查询已经上传的所有的分片信息
     *
     * @param bucketName 桶的名称
     * @param objectName 对象名称
     * @param uploadId   上传ID
     */
    @Override
    public Optional<List<Part>> listParts(String bucketName, String objectName, String uploadId) {
        bucketName = handlerBucketName(bucketName);
        Assert.notEmpty(objectName, "object name is not empty");
        Assert.notEmpty(uploadId, "uploadId is not empty");
        ListPartsRequest listPartsRequest = ListPartsRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .uploadId(uploadId)
                .build();

        // 这里因为这个请求一次最多返回1000条记录，所以需要经过多次查询
        ListPartsResponse listPartsResponse;

        List<Part> partList = new ArrayList<>();
        try {
            do {
                listPartsResponse = s3Client.listParts(listPartsRequest);
                partList.addAll(listPartsResponse.parts());
            } while (listPartsResponse.isTruncated());

            return Optional.of(partList);
        } catch (AwsServiceException | SdkClientException e) {
            log.error("list parts failed, bucket {}, objectName {}, uploadId {}, the cause is ",
                    bucketName, objectName, uploadId, e);
            return Optional.empty();
        }
    }


    /**
     * 获取预签名的对象的url
     *
     * @param bucketName    桶的名称
     * @param objectName    对象名称
     * @param signatureTime 签名url过期时间
     * @return 预签名的对象的url
     */
    @Override
    public Optional<PresignedGetObjectRequest> getPresignedUrl(String bucketName, String objectName, Duration signatureTime) {
        // 处理桶名称
        bucketName = handlerBucketName(bucketName);
        // 处理对象名称
        Assert.notEmpty(objectName, "object name is not empty");
        // 校验签名时间
        Assert.notNull(signatureTime, "signatureTime not null");

        try {
            // 获取对象的请求
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .build();

            // 获取预签名对象的请求
            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(signatureTime)
                    .getObjectRequest(getObjectRequest)
                    .build();

            // 请求获取预签名对象
            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
            String presignedGetUrl = presignedGetObjectRequest.url().toString();
            log.info("Presigned URL: {}", presignedGetUrl);
            return Optional.of(presignedGetObjectRequest);
        } catch (S3Exception e) {
            log.error("bucketName {} objectName {} signatureTime {}, get Presigned Url failed, the cause is ",
                    bucketName, objectName, signatureTime, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ResponseInputStream<GetObjectResponse>> getObject(String bucketName, String objectName) throws IOException {
        bucketName = handlerBucketName(bucketName);
        Assert.notEmpty(objectName, "objectName not empty");
        objectName = objectName.replace("\\", FILE_SEPARATOR);

        try {
            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(GetObjectRequest.builder().bucket(bucketName).key(objectName).build());
            return Optional.of(responseInputStream);
        } catch (AwsServiceException | SdkClientException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void download(String bucketName, String objectName, String downloadBasePath) throws IOException {
        Assert.notEmpty(downloadBasePath, "downloadBasePath not empty");
        Optional<ResponseInputStream<GetObjectResponse>> responseInputStreamOptional = getObject(bucketName, objectName);
        if (!responseInputStreamOptional.isPresent()) {
            log.warn("bucket {} object {} not exists, not need download", bucketName, objectName);
            return;
        }


        if (!downloadBasePath.endsWith("\\") && !downloadBasePath.endsWith(FILE_SEPARATOR)) {
            downloadBasePath = downloadBasePath + FILE_SEPARATOR;
        }

        String downloadFilePath = downloadBasePath + objectName.substring(objectName.lastIndexOf(FILE_SEPARATOR) + 1);
        File file = new File(downloadFilePath);

        if (file.exists()) {
            log.info("{} already exists, no need to download", downloadFilePath);
            return;
        }

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(responseInputStreamOptional.get());
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))
        ) {
            IOUtils.copy(bufferedInputStream, bufferedOutputStream);
            log.info("{} download success", downloadFilePath);
        }
    }


    /**
     * 获取预签名的上传URL
     *
     * @param bucketName    桶的名称
     * @param objectName    对象名称
     * @param contentType   文件类型
     * @param signatureTime 预签名put url的过期时间
     * @return 预签名的put url
     */
    public Optional<PresignedPutObjectRequest> getPresignedPutUrl(String bucketName, String objectName, String contentType, Duration signatureTime) {
        bucketName = handlerBucketName(bucketName);
        Assert.notEmpty(objectName, "object name not empty");
        Assert.notEmpty(contentType, "contentType not empty");
        Assert.notNull(signatureTime, "signatureTime not null");

        try {
            // 这里的预签名put请求，需要指定内容的类型
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .contentType(contentType)
                    .build();

            // 构建put的预签名请求
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(signatureTime)
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            return Optional.of(presignedRequest);
        } catch (S3Exception e) {
            log.error("bucketName {} objectName {} contentType {} signatureTime {}, get Presigned Put Url failed, the cause is ",
                    bucketName,
                    objectName,
                    contentType,
                    signatureTime,
                    e
            );
            return Optional.empty();
        }
    }


    /**
     * 查询桶的所有对象
     *
     * @param bucketName 桶对象
     * @return 桶中的所有对象
     */
    @Override
    public Optional<List<S3Object>> listObjects(String bucketName, String objectPrefix) {
        bucketName = handlerBucketName(bucketName);
        ListObjectsV2Request.Builder builder = ListObjectsV2Request.builder().bucket(bucketName);

        // 设置需要查询的文件夹
        if (StrUtil.isNotEmpty(objectPrefix)) {
            builder.prefix(objectPrefix);
        }

        ListObjectsV2Request listObjectsV2Request = builder.build();
        List<S3Object> s3ObjectList = null;
        try {
            ListObjectsV2Response listObjectsV2Response;
            s3ObjectList = new ArrayList<>();
            do {
                listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
                s3ObjectList.addAll(listObjectsV2Response.contents());
            } while (listObjectsV2Response.isTruncated());
            return Optional.of(s3ObjectList);
        } catch (AwsServiceException | SdkClientException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * 查询桶的所有对象
     *
     * @param bucketName 桶对象
     * @return 桶中的所有对象
     */
    @Override
    public Optional<List<S3Object>> listObjects(String bucketName) {
        return listObjects(bucketName, null);
    }


    /**
     * 合并对象的方法
     *
     * @param originBucketName 源分块对象所在的桶
     * @param destBucketName   需要合并到的桶
     * @param destObjectName   需要合并对象的名称
     * @param s3Objects        需要合并的分片对象
     */
    public Optional<CompleteMultipartUploadResponse> composeObject(String originBucketName,
                                                                   String destBucketName,
                                                                   String destObjectName,
                                                                   List<S3Object> s3Objects) {
        // 参数校验
        Assert.notEmpty(originBucketName, "originBucketName not empty");
        Assert.notEmpty(destBucketName, "destBucketName not empty");
        Assert.notEmpty(destObjectName, "destObjectName not empty");
        Assert.notEmpty(s3Objects, "s3Objects not empty");

        // 新增一个判断，如果需要复制的对象只有一个的话，那我们就调用copyObject()进行复制
        if (s3Objects.size() == SINGLETON_LIST_SIZE) {
            Optional<CopyObjectResponse> copyObjectResponseOptional = copyObject(originBucketName, s3Objects.get(0).key(), destBucketName, destObjectName);
            if (!copyObjectResponseOptional.isPresent()) {
                return Optional.empty();
            }

            CopyObjectResponse copyObjectResponse = copyObjectResponseOptional.get();
            CompleteMultipartUploadResponse completeMultipartUploadResponse = CompleteMultipartUploadResponse.builder()
                    .bucket(destBucketName)
                    .key(destObjectName)
                    .expiration(copyObjectResponse.expiration())
                    .serverSideEncryption(copyObjectResponse.serverSideEncryption())
                    .versionId(copyObjectResponse.versionId())
                    .ssekmsKeyId(copyObjectResponse.ssekmsKeyId())
                    .bucketKeyEnabled(copyObjectResponse.bucketKeyEnabled())
                    .requestCharged(copyObjectResponse.requestCharged())
                    .build();
            return Optional.of(completeMultipartUploadResponse);
        }

        // 对传入的对象集进行排序
        List<S3Object> s3ObjectList = s3Objects
                .stream()
                .sorted(Comparator.comparingInt(o -> Integer.parseInt(o.key())))
                .collect(Collectors.toList());

        return multipartUpload(destBucketName, destObjectName, s3ObjectList, s3Objects1 -> CollectionUtil.isNotEmpty(s3ObjectList),
                (targetObj, newBucketName, newObjectName, uploadId) ->
                        targetObj.stream().map(s3Object ->
                                CompletedPart.builder()
                                        .partNumber(Integer.parseInt(s3Object.key()))
                                        .eTag(s3Client.uploadPartCopy(UploadPartCopyRequest.builder()
                                                .sourceBucket(originBucketName)
                                                .sourceKey(s3Object.key())
                                                .destinationBucket(destBucketName)
                                                .destinationKey(destObjectName)
                                                .uploadId(uploadId)
                                                .partNumber(Integer.parseInt(s3Object.key()))
                                                .copy()
                                                .build()).copyPartResult().eTag())
                                        .build()

                        ).collect(Collectors.toList())

        );
    }

    @Override
    public Optional<CompleteMultipartUploadResponse> composeObject(String originBucketName, String destBucketName, String destObjectName) {
        // 参数校验
        Assert.notEmpty(originBucketName, "originBucketName not empty");
        Assert.notEmpty(destBucketName, "destBucketName not empty");
        Assert.notEmpty(destObjectName, "destObjectName not empty");

        // 查询指定桶中的所有数据
        Optional<List<S3Object>> optionalS3ObjectList = listObjects(originBucketName);
        if (!optionalS3ObjectList.isPresent()) {
            return Optional.empty();
        }

        List<S3Object> s3ObjectList = optionalS3ObjectList.get();
        return composeObject(originBucketName, destBucketName, destObjectName, s3ObjectList);
    }


    public void uploadFolder(String bucketName, String baseFolder) throws IOException {
        bucketName = handlerBucketName(bucketName);
        Assert.notEmpty(baseFolder, "baseFolder not empty");
        // 上传文件需要保持文件的目录结构
        File parentFile = new File(baseFolder);
        if (!parentFile.exists()) {
            throw new IllegalArgumentException(baseFolder + ", not exists");
        }

        if (parentFile.isFile()) {


        } else {
            // 此时才是目录拷贝
            Collection<File> fileList = FileUtils.listFiles(parentFile, null, true);
            if (CollectionUtil.isEmpty(fileList)) {

                // 无文件，无需上传
                return;
            }

            String parentPath = parentFile.getCanonicalPath().replace(File.separator, FILE_SEPARATOR);
            // 这里执行真正的上传逻辑
            String objectName;
            String objectPrefix = getUploadObjectNamePrefix();
            for (File tmpFile : fileList) {
                // 这里采用单文件直接上传
                objectName = objectPrefix + FILE_SEPARATOR + tmpFile.getCanonicalPath()
                        .replace(File.separator, FILE_SEPARATOR)
                        .replace(parentPath, parentFile.getName());


                log.info("objectName: {}", objectName);
                if (tmpFile.length() < MAX_SINGLETON_SIZE) {
                    System.out.println(putObject(bucketName, objectName, null, RequestBody.fromFile(tmpFile)));
                } else {
                    // 采用分段上传
                    System.out.println(multipartUpload(bucketName, objectName, tmpFile));
                }
            }
        }


    }


    /**
     * 下载文件夹
     *
     * @param bucketName       桶名称
     * @param objectPrefix     对象前缀，也可以理解为文件夹
     * @param downloadBasePath 下载到本地的基础路径
     * @throws IOException IOException
     */
    @Override
    public void downloadFolder(String bucketName, String objectPrefix, String downloadBasePath) throws IOException {
        bucketName = handlerBucketName(bucketName);
        ListObjectsV2Request.Builder builder = ListObjectsV2Request.builder().bucket(bucketName);
        Assert.notEmpty(downloadBasePath, "downloadBasePath not empty");

        String replacePrefix = null;
        if (StrUtil.isNotEmpty(objectPrefix)) {
            objectPrefix = objectPrefix.replace("\\", "/");
            replacePrefix = objectPrefix.substring(0, objectPrefix.lastIndexOf("/"));
            builder.prefix(objectPrefix);
        }

        ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(builder.build());
        List<S3Object> contents = listObjectsV2Response.contents();
        if (CollectionUtil.isEmpty(contents)) {
            log.info("bucket {}, object prefix {} is empty, not need download", bucketName, objectPrefix);
            return;
        }


        if (!downloadBasePath.endsWith("\\") && !downloadBasePath.endsWith(FILE_SEPARATOR)) {
            downloadBasePath = downloadBasePath + FILE_SEPARATOR;
        }

        File file = null;
        for (S3Object content : contents) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(content.key())
                    .build();

            if (StrUtil.isNotEmpty(replacePrefix)) {
                file = new File(downloadBasePath + content.key().replace(replacePrefix, ""));
            } else {
                file = new File(downloadBasePath + content.key());
            }


            if (!file.getParentFile().exists()) {
                // 创建父目录
                file.getParentFile().mkdirs();
            }


            try (ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);
                 BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))
            ) {
                IOUtils.copy(responseInputStream, bos);
                log.info("{} download success", file.getCanonicalPath());
            }
        }
    }
}
