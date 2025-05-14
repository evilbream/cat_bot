package com.baranova.tg_service.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.baranova.shared.entity.Sendable;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String state;
    private Long currentPhotoId;
    private byte[] currentPhoto;
    private String currentPhotoName;
    private Integer myCatPage;
    private Boolean notRegistered;
    private Integer viewCatPage;
    private Map<String, String> myCatsMap;


    public void clearCurrentPhoto(){
        this.currentPhoto = null;
        this.currentPhotoName = null;
        this.myCatsMap = null;
    }

    public void updateUser(Sendable sendable){
        if (sendable.getPhotoName() != null) this.setCurrentPhotoName(sendable.getPhotoName());
        if (sendable.getPhoto() != null) this.setCurrentPhoto(sendable.getPhoto());
        if (sendable.getState() != null) this.setState(sendable.getState());
        if (sendable.getMyCatPage() != null) this.setMyCatPage(sendable.getMyCatPage());
        if (sendable.getViewCatPage() != null) this.setViewCatPage(sendable.getViewCatPage());
        if (sendable.getPhotoId() != null) this.setCurrentPhotoId(Long.valueOf(sendable.getPhotoId()));
        if (sendable.getMyCatsMap() != null) this.setMyCatsMap(sendable.getMyCatsMap());
    }
}
