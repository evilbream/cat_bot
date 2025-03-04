package com.baranova.cat_bot.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReactionDTO {
    private Long userId;
    private Long photoId;
    private Integer reaction;

    public ReactionDTO(Long userId, Long photoId, Integer reaction) {
        this.userId = userId;
        this.photoId = photoId;
        this.reaction = reaction;
    }

    public static class Builder {
        private Long userId;
        private Long photoId;
        private Integer reaction;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder photoId(Long photoId) {
            this.photoId = photoId;
            return this;
        }

        public Builder reaction(Integer reaction) {
            this.reaction = reaction;
            return this;
        }

        public ReactionDTO build() {
            return new ReactionDTO(this.userId, this.photoId, this.reaction);
        }
    }

}
