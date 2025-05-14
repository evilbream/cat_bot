package com.baranova.cat_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.baranova.cat_service.entity.Photo;
import com.baranova.cat_service.entity.Reaction;

public interface ReactionRepository extends JpaRepository<Reaction, UUID> {
    Optional<Photo> findById(Long id);

    Optional<Reaction> findByUserAndPhoto(Long user, Photo photo);

    List<Reaction> findAllByPhotoAndReaction(Photo photo, Integer reaction);
}
