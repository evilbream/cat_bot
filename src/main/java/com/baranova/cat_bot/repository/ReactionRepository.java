package com.baranova.cat_bot.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.baranova.cat_bot.entity.User;
import com.baranova.cat_bot.entity.Photo;
import com.baranova.cat_bot.entity.Reaction;

public interface ReactionRepository extends JpaRepository<Reaction, UUID> {
    Optional<Photo> findById(Long id);

    Optional<Reaction> findByUserAndPhoto(User user, Photo photo);

    List<Reaction> findAllByPhotoAndReaction(Photo photo, Integer reaction);
}
