package com.amazon.s3.v2.factory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.amazon.s3.v2.config.S3V2Base;
import com.amazon.s3.v2.template.AmazonS3V2Template;
import com.amazon.s3.v2.utils.BucketUtil;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * @author liuyangfang
 * @description AmazonS3V2Template的工厂类
 * @since 2023/6/5 10:11:10
 */
public class AmazonS3V2Factory {
    /**
     * 解决
     * https://github.com/aws/aws-sdk-java-v2/issues/3987
     */
    private final ExecutionInterceptor endpointHandlerExecutionInterceptor = new ExecutionInterceptor() {

        @Override
        public void beforeMarshalling(Context.BeforeMarshalling context, ExecutionAttributes executionAttributes) {
            Endpoint endpoint = executionAttributes.getAttribute(SdkInternalExecutionAttribute.RESOLVED_ENDPOINT);
            if (endpoint != null) {
                Optional<String> bucketOption = context.request().getValueForField("Bucket", String.class);
                bucketOption.ifPresent(bucketName -> {
                    // 如果Bucket包含局点，我们需要重新处理Url
                    if (BucketUtil.isLikeHost(bucketName)) {
                        //
                        URI clientUrl = executionAttributes.getAttribute(SdkInternalExecutionAttribute.CLIENT_ENDPOINT);

                        URI url = endpoint.url();

                        // 解决当bucketName包含句点(dots(.))时，java.lang.NullPointerException: host must not be null.
                        if (StrUtil.isEmpty(url.getHost())) {
                            String scheme = url.getScheme();
                            String userInfo = url.getUserInfo();
                            String host = clientUrl.getHost();
                            int port = url.getPort() == -1 ? clientUrl.getPort() : url.getPort();
                            String path = StrUtil.isEmpty(url.getPath()) ? "/" + bucketName : url.getPath();
                            String query = url.getQuery();
                            String fragment = url.getFragment();

                            try {
                                URI newUrl = new URI(scheme, userInfo, host, port, path, query, fragment);
                                Endpoint newEndpoint = endpoint.toBuilder().url(newUrl).build();
                                executionAttributes.putAttribute(SdkInternalExecutionAttribute.RESOLVED_ENDPOINT, newEndpoint);
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

        }
    };


    /**
     * 创建Amazon S3 V2的标准客户端
     *
     * @param endPoint  oss服务器地址，或者是访问url
     * @param region    区域
     * @param accessKey 访问的凭证名
     * @param secretKey 访问的凭证密码
     * @return S3Client Amazon S3 V2的标准客户端
     * @throws URISyntaxException    URISyntaxException
     * @throws MalformedURLException MalformedURLException
     */
    public S3Client createS3Client(String endPoint, String region, String accessKey, String secretKey) throws URISyntaxException, MalformedURLException {
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
                .region(Region.of(region)) // 指定region
                .credentialsProvider(() -> credentials) // 提供认证凭证信息
                .endpointOverride(new URI(endPoint)) // 提供存储服务器的url
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(false)
                        .chunkedEncodingEnabled(false)
                        .build())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .addExecutionInterceptor(endpointHandlerExecutionInterceptor)
                        .build())
                .build();
    }

    /**
     * 创建Amazon S3 V2的异步操作客户端
     *
     * @param endPoint  oss服务器地址，或者是访问url
     * @param region    区域
     * @param accessKey 访问的凭证名
     * @param secretKey 访问的凭证密码
     * @return S3Client Amazon S3 V2的标准客户端
     * @throws URISyntaxException    URISyntaxException
     * @throws MalformedURLException MalformedURLException
     */
    public S3AsyncClient createS3AsynClient(String endPoint, String region, String accessKey, String secretKey) throws URISyntaxException, MalformedURLException {
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3AsyncClient.builder()
                .region(Region.of(region)) // 指定region
                .credentialsProvider(() -> credentials) // 提供认证凭证信息
                .endpointOverride(new URI(endPoint)) // 提供存储服务器的url
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(false)
                        .chunkedEncodingEnabled(false)
                        .build())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .addExecutionInterceptor(endpointHandlerExecutionInterceptor)
                        .build())
                .build();
    }


    /**
     * 创建一个S3TransferManager
     *
     * @param s3AsyncClient s3AsyncClient
     * @return S3TransferManager
     */
    public S3TransferManager createS3TransferManager(S3AsyncClient s3AsyncClient) {
        return S3TransferManager.builder().s3Client(s3AsyncClient).build();
    }


    /**
     * 创建Amazon S3 V2的预签名的客户端
     *
     * @param endPoint  oss服务器地址，或者是访问url
     * @param region    区域
     * @param accessKey 访问的凭证名
     * @param secretKey 访问的凭证密码
     * @return S3Presigner Amazon S3 V2的预签名的客户端
     * @throws URISyntaxException URISyntaxException
     */
    public S3Presigner createS3Presigner(String endPoint, String region, String accessKey, String secretKey) throws URISyntaxException, NoSuchFieldException, IllegalAccessException {
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region)) // 指定region
                .credentialsProvider(() -> credentials) // 提供认证凭证信息
                .endpointOverride(new URI(endPoint)) // 提供存储服务器的url
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(false)
                        .chunkedEncodingEnabled(false)
                        .build())
                .build();

        // 反射注入拦截器
        Field clientInterceptorsField = presigner.getClass().getDeclaredField("clientInterceptors");
        clientInterceptorsField.setAccessible(true);
        List<ExecutionInterceptor> clientInterceptors = (List<ExecutionInterceptor>) clientInterceptorsField.get(presigner);
        clientInterceptors.add(endpointHandlerExecutionInterceptor);
        clientInterceptorsField.setAccessible(false);
        return presigner;
    }


    /**
     * 创建AmazonS3V2Template
     *
     * @param s3Client             s3Client
     * @param s3AsyncClient        s3AsyncClient
     * @param s3Presigner          s3Presigner
     * @param amazonS3V2Properties amazonS3V2Properties
     * @return AmazonS3V2Template
     */
    public AmazonS3V2Template createAmazonS3V2Template(
            S3Client s3Client, S3AsyncClient s3AsyncClient, S3TransferManager s3TransferManager, S3Presigner s3Presigner, S3V2Base amazonS3V2Properties) {
        return new AmazonS3V2Template(s3Client, s3AsyncClient, s3TransferManager, s3Presigner, amazonS3V2Properties);
    }


    /**
     * 创建Amazon S3 V2的预签名的客户端
     *
     * @param endPoint   oss服务器地址，或者是访问url
     * @param region     区域
     * @param accessKey  访问的凭证名
     * @param secretKey  访问的凭证密码
     * @param bucketName 默认存储的桶的名称
     * @return AmazonS3V2Template
     */
    public AmazonS3V2Template createAmazonS3V2Template(String endPoint, String region, String accessKey, String secretKey, String bucketName) throws MalformedURLException, URISyntaxException, NoSuchFieldException, IllegalAccessException {
        return createAmazonS3V2Template(S3V2Base.builder()
                .endPoint(endPoint)
                .region(region)
                .accessKey(accessKey)
                .secretKey(secretKey)
                .bucket(bucketName)
                .build());
    }


    /**
     * 创建Amazon S3 V2的预签名的客户端
     *
     * @param s3V2Base S3V2Base
     * @return AmazonS3V2Template
     */
    public AmazonS3V2Template createAmazonS3V2Template(S3V2Base s3V2Base) throws MalformedURLException, URISyntaxException, NoSuchFieldException, IllegalAccessException {
        Assert.notNull(s3V2Base, "s3V2Base not null");
        String endPoint = s3V2Base.getEndPoint();
        String region = s3V2Base.getRegion();
        String accessKey = s3V2Base.getAccessKey();
        String secretKey = s3V2Base.getSecretKey();

        S3Client s3Client = createS3Client(endPoint, region, accessKey, secretKey);
        S3AsyncClient s3AsyncClient = createS3AsynClient(endPoint, region, accessKey, secretKey);
        S3TransferManager s3TransferManager = createS3TransferManager(s3AsyncClient);
        S3Presigner s3Presigner = createS3Presigner(endPoint, region, accessKey, secretKey);


        return createAmazonS3V2Template(s3Client, s3AsyncClient, s3TransferManager, s3Presigner, BeanUtil.toBean(s3V2Base, S3V2Base.class));
    }
}
