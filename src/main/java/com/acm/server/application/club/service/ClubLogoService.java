// com/acm/server/application/club/service/ClubLogoService.java
package com.acm.server.application.club.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.acm.server.application.club.port.in.UpdateClubLogoUseCase;
import com.acm.server.application.club.port.out.FindClubPort;
import com.acm.server.application.club.port.out.FileStoragePort;
import com.acm.server.application.club.port.out.UpdateClubLogoPort;
import com.acm.server.common.exception.ResourceNotFoundException;
import com.acm.server.domain.Club;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubLogoService implements UpdateClubLogoUseCase {

    private final FindClubPort findClubPort;             // 도메인 조회
    private final FileStoragePort fileStoragePort;       // S3 업/삭
    private final UpdateClubLogoPort updateClubLogoPort; // DB logoUrl만 갱신

    @Transactional
    @Override
    public Club updateLogo(Long clubId, MultipartFile file) {
        validateFile(file);

        // 1) 클럽 조회 (도메인)
        Club club = findClubPort.findClub(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found: " + clubId));

        String oldUrl = club.getLogoUrl();

        // 2) 새 랜덤 키 생성 후 업로드
        String newKey = fileStoragePort.clubLogoKey(clubId, file.getOriginalFilename()); // 랜덤 키
        try (var in = file.getInputStream()) {
            var up = fileStoragePort.upload(newKey, file.getContentType(), file.getSize(), in);

            // 3) DB에 새 URL 반영
            updateClubLogoPort.updateClubLogo(clubId, up.url());
            club.setLogoUrl(up.url()); // 호출자 편의용

            // 4) 업로드 성공했으니 이전 파일 삭제(있다면)
            deleteOldIfExists(oldUrl);

            return club;
        } catch (IOException e) {
            throw new RuntimeException("S3 upload failed", e);
        }
    }

    private void deleteOldIfExists(String oldUrl) {
        if (oldUrl == null || oldUrl.isBlank()) return;
        try {
            String marker = ".amazonaws.com/";
            int idx = oldUrl.indexOf(marker);
            if (idx > 0) {
                String oldKey = oldUrl.substring(idx + marker.length());
                fileStoragePort.delete(oldKey);
            }
        } catch (Exception ignore) {
            // 이전 파일 삭제 실패는 치명적이지 않음 (로그만 고려)
        }
    }

    private void validateFile(MultipartFile f) {
        if (f == null || f.isEmpty()) throw new IllegalArgumentException("빈 파일");
        if (f.getSize() > 5 * 1024 * 1024) throw new IllegalArgumentException("최대 5MB");
        if (f.getContentType() == null || !f.getContentType().startsWith("image/"))
            throw new IllegalArgumentException("이미지 파일만 업로드 가능");
    }
}
