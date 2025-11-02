package com.acm.server.application.club.port.in;


import org.springframework.web.multipart.MultipartFile;
import com.acm.server.domain.Club;

/**
 * ✅ 클럽 로고 업로드/교체 유스케이스 (in port)
 *
 * Controller는 이 인터페이스만 의존하고,
 * 실제 로직은 Service 계층(implements UpdateClubLogoUseCase)에서 처리한다.
 */
public interface UpdateClubLogoUseCase {

    /**
     * clubId에 해당하는 클럽의 로고를 업로드하거나 기존 로고를 교체한다.
     * 
     * @param clubId 클럽 ID
     * @param file 업로드할 이미지 파일 (Multipart)
     * @return 업데이트된 ClubEntity (logoUrl 포함)
     */
    Club updateLogo(Long clubId, MultipartFile file);
}
