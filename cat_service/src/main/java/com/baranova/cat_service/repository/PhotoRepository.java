package com.baranova.cat_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.baranova.cat_service.entity.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    Optional<Photo> findById(Long id);

    Page<Photo> findByAuthor(Long author, Pageable pageable);

    boolean existsById(Long id);

    void deleteById(Long id);
}
