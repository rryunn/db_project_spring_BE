// com/acm/server/adapter/out/external/s3/S3ClientConfig.java
package com.acm.server.adapter.out.external.s3;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
public class S3ClientConfig {

    @Bean
    public S3Client s3Client(S3Properties props, Environment env) {
        // .env나 application.yml에서 읽은 키
        String accessKey = env.getProperty("AWS_ACCESS_KEY_ID");
        String secretKey = env.getProperty("AWS_SECRET_ACCESS_KEY");

        AwsCredentialsProvider provider;

        if (accessKey != null && secretKey != null &&
            !accessKey.isBlank() && !secretKey.isBlank()) {
            // 로컬 테스트용: 환경변수 키 직접 사용
            provider = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
            );
        } else {
            // 배포 환경(EC2, IAM Role)에서는 자동으로 Role 자격증명 사용
            provider = DefaultCredentialsProvider.create();
        }

        return S3Client.builder()
                .region(Region.of(props.region()))
                .credentialsProvider(provider)
                .build();
    }
}
