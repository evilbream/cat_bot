package com.baranova.cat_service.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;


@Getter
@RequiredArgsConstructor
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

    public static class Builder {
        private Long id;
        private Long author;
        private String username;
        private String catName;
        private LocalDateTime uploadedAt;
        private byte[] photo;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder author(Long author) {
            this.author = author;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder catName(String catName) {
            this.catName = catName;
            return this;
        }

        public Builder uploadedAt(LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public Builder photo(byte[] photo) {
            this.photo = photo;
            return this;
        }

        public CatDTO build() {
            return new CatDTO(id, author, username, catName, uploadedAt, photo);
        }
    }

}
