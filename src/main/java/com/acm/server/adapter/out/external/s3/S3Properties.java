// com/acm/server/adapter/out/external/s3/S3Properties.java
package com.acm.server.adapter.out.external.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "s3")
public record S3Properties(
        String bucket,  // 예: acmimage
        String region   // 예: ap-northeast-2
) {
    // CloudFront 안 쓰므로 S3 퍼블릭 URL 생성
    public String publicUrl(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }
}
