package com.baranova.cat_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.baranova.cat_bot.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findById(Long id);
}
