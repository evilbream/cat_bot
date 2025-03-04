package com.baranova.cat_bot.dto.converters;

import com.baranova.cat_bot.dto.CatDTO;
import com.baranova.cat_bot.entity.Photo;
import com.baranova.cat_bot.entity.User;

public class CatConverter {
    public static CatDTO fromEntity(Photo entity) {
        if (entity == null) return null;
        return new CatDTO.Builder()
                .id(entity.getId())
                .author(entity.getAuthor() != null ? entity.getAuthor().getId() : null)
                .username(entity.getAuthor() != null ? entity.getAuthor().getUsername() : null)
                .catName(entity.getCatName())
                .uploadedAt(entity.getUploadedAt())
                .photo(entity.getPhoto())
                .build();
    }

    public static Photo toEntity(CatDTO photo, User user) {
        Photo photoEntity = new Photo();
        photoEntity.setId(photo.getId());
        photoEntity.setAuthor(user);
        photoEntity.setCatName(photo.getCatName());
        photoEntity.setUploadedAt(photo.getUploadedAt());
        photoEntity.setPhoto(photo.getPhoto());
        return photoEntity;
    }


}
