package com.baranova.cat_service.dto.converters;

import com.baranova.cat_service.dto.ReactionDTO;
import com.baranova.cat_service.entity.Photo;
import com.baranova.cat_service.entity.Reaction;
import com.baranova.cat_service.entity.User;

public class ReactionConverter {
    public static ReactionDTO fromEntity(Reaction reaction) {
        return new ReactionDTO
                .Builder()
                .userId(reaction.getUser().getId())
                .photoId(reaction.getPhoto().getId())
                .reaction(reaction.getReaction())
                .build();
    }

    public static Reaction toEntity(User user, Photo photo, ReactionDTO reactionDto) {
        Reaction reaction = new Reaction();
        reaction.setReaction(reactionDto.getReaction());
        reaction.setPhoto(photo);
        reaction.setUser(user);
        return reaction;
    }
}
