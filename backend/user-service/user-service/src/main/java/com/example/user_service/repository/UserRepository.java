package com.example.user_service.repository;

import com.example.user_service.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
    boolean existsByEmail(String email);

    Boolean existsByKeyCloakId(String userId);

    User findByEmail(String email);

    Optional<Object> findByKeyCloakId(String keycloakId);
}
