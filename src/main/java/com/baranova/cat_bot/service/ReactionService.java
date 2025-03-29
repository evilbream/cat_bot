package com.baranova.cat_bot.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import com.baranova.cat_bot.dto.ReactionDTO;
import com.baranova.cat_bot.dto.converters.ReactionConverter;
import com.baranova.cat_bot.entity.Photo;
import com.baranova.cat_bot.entity.Reaction;
import com.baranova.cat_bot.entity.User;
import com.baranova.cat_bot.repository.PhotoRepository;
import com.baranova.cat_bot.repository.ReactionRepository;
import com.baranova.cat_bot.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ReactionService {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    @Async
    @Transactional
    public void updateReaction(ReactionDTO reactionDTO) {
        User user = userRepository.findById(reactionDTO.getUserId()).orElse(null);
        Photo photo = photoRepository.findById(reactionDTO.getPhotoId()).orElse(null);

        Reaction existingReaction = reactionRepository.findByUserAndPhoto(user, photo).orElse(null);
        if (existingReaction != null) {
            if (existingReaction.getReaction() == reactionDTO.getReaction()) return;

            // update existing reaction
            existingReaction.setReaction(reactionDTO.getReaction());
            reactionRepository.save(existingReaction);
            return;
        }

        Reaction reaction = ReactionConverter.toEntity(user, photo, reactionDTO);
        reactionRepository.save(reaction);
    }

    public Integer getReactionCount(Long photoId, Integer react) {
        Photo photo = new Photo();
        photo.setId(photoId);
        return reactionRepository.findAllByPhotoAndReaction(photo, react).size();
    }


}
