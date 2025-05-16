package com.example.media_service.repository;

import com.example.media_service.model.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, Long> {
    Optional<ProfilePicture> findByUserId(Long userId);
}
