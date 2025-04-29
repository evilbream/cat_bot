package com.baranova.cat_service.dto.converters;

import com.baranova.cat_service.dto.ReactionDTO;
import com.baranova.cat_service.entity.Photo;
import com.baranova.cat_service.entity.Reaction;

public class ReactionConverter {
    public static ReactionDTO fromEntity(Reaction reaction) {
        return ReactionDTO
                .builder()
                .userId(reaction.getUser())
                .photoId(reaction.getPhoto().getId())
                .reaction(reaction.getReaction())
                .build();
    }

    public static Reaction toEntity(Long userId, Photo photo, ReactionDTO reactionDto) {
        Reaction reaction = new Reaction();
        reaction.setReaction(reactionDto.getReaction());
        reaction.setPhoto(photo);
        reaction.setUser(userId);
        return reaction;
    }
}
