package com.baranova.cat_service.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import com.baranova.cat_service.dto.ReactionDTO;
import com.baranova.cat_service.dto.converters.ReactionConverter;
import com.baranova.cat_service.entity.Photo;
import com.baranova.cat_service.entity.Reaction;
import com.baranova.cat_service.repository.PhotoRepository;
import com.baranova.cat_service.repository.ReactionRepository;

import jakarta.transaction.Transactional;

@Service
public class ReactionService {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    @Async
    @Transactional
    public void updateReaction(ReactionDTO reactionDTO) {
        Long user = reactionDTO.getUserId();
        Photo photo = photoRepository.findById(reactionDTO.getPhotoId()).orElse(null);
        if (photo == null) {
            throw new IllegalArgumentException("Photo not found");
        }

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
