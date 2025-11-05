package com.acm.server.application.recruitment.port.in;

import com.acm.server.adapter.in.security.JwtUserPrincipal;
import com.acm.server.domain.RecruitmentImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ManageRecruitmentImagesUseCase {
    List<RecruitmentImage> upload(JwtUserPrincipal principal, Long recruitmentId, List<MultipartFile> files);
    RecruitmentImage replaceByUrl(JwtUserPrincipal principal, Long recruitmentId, String oldUrl, MultipartFile newFile);
    void deleteOneByUrl(JwtUserPrincipal principal, Long recruitmentId, String url);
    void deleteAll(JwtUserPrincipal principal, Long recruitmentId);
}
