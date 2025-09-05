package com.acm.server.domain;


import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class ClubActivityImage {
    private final Long id;
    private final Long clubId;
    private final String imageUrl;
}
