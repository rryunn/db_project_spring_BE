package com.acm.server.application.clubimage.port.in;

import com.acm.server.domain.ClubActivityImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ManageClubActivityImagesUseCase {

    /** 처음/추가 업로드 (여러 장 가능) */
    List<ClubActivityImage> upload(Long clubId, List<MultipartFile> files);

    /** oldUrl 기준 교체 */
    ClubActivityImage replaceByUrl(Long clubId, String oldUrl, MultipartFile newFile);

    /** URL 한 장 삭제 */
    void deleteOneByUrl(Long clubId, String url);

    /** 해당 클럽의 모든 사진 삭제 */
    void deleteAll(Long clubId);
}