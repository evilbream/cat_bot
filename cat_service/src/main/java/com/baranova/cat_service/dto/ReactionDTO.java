package com.baranova.cat_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class ReactionDTO {
    private Long userId;
    private Long photoId;
    private Integer reaction;


}
