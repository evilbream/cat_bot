package com.baranova.cat_service.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;


@Getter
@RequiredArgsConstructor
@Builder
public class CatDTO {
    private Long id;
    private Long author;
    private String username;
    private String catName;
    private LocalDateTime uploadedAt;
    private byte[] photo;

    public CatDTO(Long id, Long author, String username, String catName, LocalDateTime uploadedAt, byte[] photo) {
        this.id = id;
        this.author = author;
        this.username = username;
        this.catName = catName;
        this.uploadedAt = uploadedAt;
        this.photo = photo;
    }

}
