// com/acm/server/adapter/out/external/s3/S3StorageAdapter.java
package com.acm.server.adapter.out.external.s3;

import com.acm.server.application.club.port.out.FileStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3StorageAdapter implements FileStoragePort {

    private final S3Client client;
    private final S3Properties props;

    @Override
    public Uploaded upload(String key, String contentType, long len, InputStream body) {
        var req = PutObjectRequest.builder()
                .bucket(props.bucket())
                .key(key)
                .contentType(contentType)
                .cacheControl("public, max-age=31536000, immutable")
                .build();
        var res = client.putObject(req, RequestBody.fromInputStream(body, len));
        return new Uploaded(key, props.publicUrl(key), res.eTag());
    }

    @Override
    public void delete(String key) {
        client.deleteObject(DeleteObjectRequest.builder()
                .bucket(props.bucket())
                .key(key)
                .build());
    }

    @Override
    public String clubLogoKey(Long clubId, @Nullable String originalFilename) {
        String ext = ext(originalFilename);
        if (ext.isBlank()) ext = ".png";
        String uuid = UUID.randomUUID().toString();
        return "clublogo/%d/logo-%s%s".formatted(clubId, uuid, ext);
    }

    @Override
    public String clubActivityKey(Long clubId, @Nullable String originalFilename) {
        String ext = ext(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return "clubActivityImages/%d/%s%s".formatted(clubId, uuid, ext);
    }

    @Override
    public String recruitmentImageKey(Long recruitmentId, @Nullable String originalFilename) {
        String ext = ext(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return "recruitment/%d/%s%s".formatted(recruitmentId, uuid, ext);
    }

    private String ext(String name) {
        if (name == null) return "";
        int i = name.lastIndexOf('.');
        return (i >= 0) ? name.substring(i).toLowerCase() : "";
    }
}
