package com.acm.server.application.recruitment.service;

import com.acm.server.adapter.in.security.JwtUserPrincipal;
import com.acm.server.application.club.port.out.FileStoragePort;
import com.acm.server.application.recruitment.port.in.ManageRecruitmentImagesUseCase;
import com.acm.server.application.recruitment.port.out.FindRecruitmentPort;
import com.acm.server.application.recruitment.port.out.ManageRecruitmentImagesPort;
import com.acm.server.application.recruitment.port.out.RecruitmentImageRedisPort;
import com.acm.server.domain.Recruitment;
import com.acm.server.domain.RecruitmentImage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ManageRecruitmentImagesService implements ManageRecruitmentImagesUseCase {
    private final FindRecruitmentPort findRecruitmentPort;       // 모집공고 조회 (권한 검사용)
    private final ManageRecruitmentImagesPort imageDbPort;       // 모집공고 이미지 DB (Out)
    private final FileStoragePort fileStoragePort;             // 파일 스토리지 (Out)
    private final RecruitmentImageRedisPort recruitmentImageRedisPort; 

    @Override
    public List<RecruitmentImage> upload(JwtUserPrincipal principal, Long recruitmentId, List<MultipartFile> files) {
        validateFiles(files);
        // 1. 권한 검사
        checkPermission(principal, recruitmentId);

        List<RecruitmentImage> saved = new ArrayList<>();
        for (MultipartFile file : files) {
            try (InputStream in = file.getInputStream()) {
                // 2. FileStoragePort의 recruitmentImageKey 사용
                String key = fileStoragePort.recruitmentImageKey(recruitmentId, file.getOriginalFilename());
                var uploaded = fileStoragePort.upload(key, file.getContentType(), file.getSize(), in);

                // 3. RecruitmentImage 도메인 생성 및 DB 저장
                var domain = RecruitmentImage.builder()
                        .id(null)
                        .recruitmentId(recruitmentId)
                        .imageUrl(uploaded.url())
                        .build();
                saved.add(imageDbPort.save(domain));
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload recruitment image", e);
            }
        }
        recruitmentImageRedisPort.evictImageUrls(recruitmentId);
        return saved.stream().sorted(Comparator.comparing(RecruitmentImage::getId)).toList();
    }

    @Override
    public RecruitmentImage replaceByUrl(JwtUserPrincipal principal, Long recruitmentId, String oldUrl, MultipartFile newFile) {
        validateFile(newFile);
        // 1. 권한 검사
        checkPermission(principal, recruitmentId);

        // 2. 대상 이미지 존재 확인
        imageDbPort.findByRecruitmentIdAndUrl(recruitmentId, oldUrl)
                .orElseThrow(() -> new IllegalArgumentException("Image not found for this recruitment/url"));

        try (InputStream in = newFile.getInputStream()) {
            String key = fileStoragePort.recruitmentImageKey(recruitmentId, newFile.getOriginalFilename());
            var uploaded = fileStoragePort.upload(key, newFile.getContentType(), newFile.getSize(), in);

            // 3. DB 교체 (삭제 후 생성)
            imageDbPort.deleteByRecruitmentIdAndUrl(recruitmentId, oldUrl);

            var newDomain = RecruitmentImage.builder()
                    .id(null)
                    .recruitmentId(recruitmentId)
                    .imageUrl(uploaded.url())
                    .build();
            var saved = imageDbPort.save(newDomain);

            recruitmentImageRedisPort.evictImageUrls(recruitmentId);

            return saved;
        } catch (Exception e) {
            throw new RuntimeException("Failed to replace recruitment image", e);
        }
    }

    @Override
    public void deleteOneByUrl(JwtUserPrincipal principal, Long recruitmentId, String url) {
        // 1. 권한 검사
        checkPermission(principal, recruitmentId);

        // 2. 대상 이미지 존재 확인
        imageDbPort.findByRecruitmentIdAndUrl(recruitmentId, url)
                .orElseThrow(() -> new IllegalArgumentException("Image not found for this recruitment/url"));

        // 3. DB에서 삭제
        imageDbPort.deleteByRecruitmentIdAndUrl(recruitmentId, url);

        recruitmentImageRedisPort.evictImageUrls(recruitmentId);
    }

    @Override
    public void deleteAll(JwtUserPrincipal principal, Long recruitmentId) {
        // 1. 권한 검사
        checkPermission(principal, recruitmentId);

        // 2. (선택) S3 파일 일괄 삭제

        // 3. DB에서 일괄 삭제

        imageDbPort.deleteAllByRecruitmentId(recruitmentId);
        recruitmentImageRedisPort.evictImageUrls(recruitmentId);
    }

    /**
     * [핵심] 서비스 내부 권한 검사
     * recruitmentId로 Recruitment를 찾아 clubId를 꺼낸 뒤, principal과 비교
     */
    private void checkPermission(JwtUserPrincipal principal, Long recruitmentId) {
        Recruitment recruitment = findRecruitmentPort.findRecruitmentById(recruitmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruitment not found: " + recruitmentId));

        Long clubId = recruitment.getClubId();

        if (principal.managedClubs() == null || !principal.managedClubs().contains(clubId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
    }

    // --- 파일 유효성 검사 (공통) ---
    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) throw new IllegalArgumentException("Files are required");
        files.forEach(this::validateFile);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File is empty");
        // (필요시) 용량, 타입 검사 추가
    }
}

