// com/acm/server/application/club/port/out/FileStoragePort.java
package com.acm.server.application.club.port.out;

import java.io.InputStream;

public interface FileStoragePort {

    record Uploaded(String key, String url, String eTag) {}

    Uploaded upload(String key, String contentType, long contentLength, InputStream body);
    void delete(String key);

    // ✅ 로고도 랜덤 키(파일명 매번 변경)
    String clubLogoKey(Long clubId, String originalFilename);

    // 확장: 활동사진/모집공고용 (여러 장)
    String clubActivityKey(Long clubId, String originalFilename);
    String recruitmentImageKey(Long recruitmentId, String originalFilename);
}
