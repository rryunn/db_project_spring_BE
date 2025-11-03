package com.acm.server.application.club.service;

import com.acm.server.application.club.port.out.FileStoragePort;
import com.acm.server.application.club.port.out.FindClubPort;
import com.acm.server.application.club.port.out.ManageClubActivityImagesPort; // DB Port (네가 준 인터페이스)
import com.acm.server.application.club.port.in.ManageClubActivityImagesUseCase; // 유즈케이스 Port In
import com.acm.server.domain.ClubActivityImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ManageClubActivityImagesService implements ManageClubActivityImagesUseCase {

    private final FindClubPort findClubPort;                       // club 존재 검증용
    private final ManageClubActivityImagesPort imageDbPort;        // DB Port (out)
    private final FileStoragePort fileStoragePort;                 // 파일 스토리지 Port (out)

    @Override
    public List<ClubActivityImage> upload(Long clubId, List<MultipartFile> files) {
        validateFiles(files);
        // 클럽 존재 검증
        findClubPort.findClub(clubId).orElseThrow(() -> new IllegalArgumentException("club not found: " + clubId));

        List<ClubActivityImage> saved = new ArrayList<>();

        for (MultipartFile file : files) {
            try (InputStream in = file.getInputStream()) {
                String key = fileStoragePort.clubActivityKey(clubId, file.getOriginalFilename());
                var uploaded = fileStoragePort.upload(
                        key,
                        file.getContentType(),
                        file.getSize(),
                        in
                );
                var domain = ClubActivityImage.builder()
                        .id(null)
                        .clubId(clubId)
                        .imageUrl(uploaded.url()) // 키 미보관 정책
                        .build();
                saved.add(imageDbPort.save(domain));
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload club activity image", e);
            }
        }

        // 업로드 순서 = PK 오름차순 가정
        return saved.stream()
                .sorted(Comparator.comparing(ClubActivityImage::getId))
                .toList();
    }

    @Override
    public ClubActivityImage replaceByUrl(Long clubId, String oldUrl, MultipartFile newFile) {
        if (oldUrl == null || oldUrl.isBlank()) throw new IllegalArgumentException("oldUrl is required");
        validateFile(newFile);

        // 대상 이미지가 해당 club 소유인지 확인
        imageDbPort.findByClubIdAndUrl(clubId, oldUrl)
                .orElseThrow(() -> new IllegalArgumentException("image not found for this club/url"));

        try (InputStream in = newFile.getInputStream()) {
            String key = fileStoragePort.clubActivityKey(clubId, newFile.getOriginalFilename());
            var uploaded = fileStoragePort.upload(
                    key,
                    newFile.getContentType(),
                    newFile.getSize(),
                    in
            );

            // DB 교체 전략: 기존 레코드 삭제 → 새 레코드 생성 (정렬은 PK ASC이므로 맨 뒤로 감)
            imageDbPort.deleteByClubIdAndUrl(clubId, oldUrl);

            var newDomain = ClubActivityImage.builder()
                    .id(null)
                    .clubId(clubId)
                    .imageUrl(uploaded.url())
                    .build();

            var saved = imageDbPort.save(newDomain);

            // ⚠️ 키 미보관 정책: 스토리지의 이전 파일 삭제는 수행하지 않음.
            return saved;
        } catch (Exception e) {
            throw new RuntimeException("Failed to replace club activity image", e);
        }
    }

    @Override
    public void deleteOneByUrl(Long clubId, String url) {
        if (url == null || url.isBlank()) throw new IllegalArgumentException("url is required");

        // 소유 검증 겸 존재 확인
        imageDbPort.findByClubIdAndUrl(clubId, url)
                .orElseThrow(() -> new IllegalArgumentException("image not found for this club/url"));

        imageDbPort.deleteByClubIdAndUrl(clubId, url);

        // ⚠️ 키 미보관 정책: 스토리지의 파일 삭제는 수행하지 않음.
    }

    @Override
    public void deleteAll(Long clubId) {
        var all = imageDbPort.findAllByClubId(clubId);
        if (all.isEmpty()) return;

        imageDbPort.deleteAllByClubId(clubId);

        // ⚠️ 키 미보관 정책: 스토리지 일괄 삭제는 수행하지 않음.
    }

    /* ---------- 내부 유효성 ---------- */
    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty())
            throw new IllegalArgumentException("files is required");
        files.forEach(this::validateFile);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("file is empty");
    }
}