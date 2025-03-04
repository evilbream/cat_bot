package com.baranova.cat_bot.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String state;
    private CatDTO currentPhoto;
    private Integer myCatPage;
    private Boolean notRegistered;

    public UserDTO(Long id,
                   String username,
                   String state,
                   CatDTO currentPhoto,
                   Integer myCatPage,
                   Boolean notRegistered) {
        this.id = id;
        this.username = username;
        this.state = state;
        this.currentPhoto = currentPhoto;
        this.myCatPage = myCatPage;
        this.notRegistered = notRegistered;
    }

    public static class Builder {
        private Long id;
        private String username;
        private String state;
        private CatDTO currentPhoto;
        private Integer myCatPage = 0;
        private Boolean notRegistered = false;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder currentPhoto(CatDTO currentPhoto) {
            this.currentPhoto = currentPhoto;
            return this;
        }

        public Builder myCatPage(Integer myCatPage) {
            this.myCatPage = myCatPage;
            return this;
        }

        public Builder notRegistered(Boolean notRegistered) {
            this.notRegistered = notRegistered;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(id, username, state, currentPhoto, myCatPage, notRegistered);
        }
    }

}
