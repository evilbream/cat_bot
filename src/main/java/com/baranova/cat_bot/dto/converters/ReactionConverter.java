package com.baranova.cat_bot.dto.converters;

import com.baranova.cat_bot.dto.ReactionDTO;
import com.baranova.cat_bot.entity.Photo;
import com.baranova.cat_bot.entity.Reaction;
import com.baranova.cat_bot.entity.User;

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
