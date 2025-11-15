// com/acm/server/adapter/out/external/s3/S3Properties.java
package com.acm.server.adapter.out.external.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "s3")
public record S3Properties(
        String bucket,  
        String region   
) {
    public String publicUrl(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }
}
