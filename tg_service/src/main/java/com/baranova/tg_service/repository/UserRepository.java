package com.baranova.tg_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.baranova.tg_service.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findById(Long id);
}

