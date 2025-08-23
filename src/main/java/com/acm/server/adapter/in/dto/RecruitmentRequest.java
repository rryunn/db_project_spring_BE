package com.acm.server.adapter.in.dto;

import com.acm.server.adapter.out.entity.RecruitmentType;
import com.acm.server.application.recruitment.port.in.RecruitmentCommand;

import java.time.LocalDate;

public record RecruitmentRequest (
    String title,
    String description,
    RecruitmentType type,
    String phoneNumber,
    String email,
    LocalDate startDate,
    LocalDate endDate,
    String url
){
    public RecruitmentCommand toCommand(Long clubId){
        return new RecruitmentCommand(
                clubId,title,description,type,phoneNumber,email,startDate,endDate,url
        );
    }
}
