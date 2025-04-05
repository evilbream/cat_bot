package com.baranova.tg_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String state;
    private byte[] currentPhoto;
    private String currentPhotoName;
    private Integer myCatPage;
    private Boolean notRegistered;
    private Integer viewCatPage;

}
