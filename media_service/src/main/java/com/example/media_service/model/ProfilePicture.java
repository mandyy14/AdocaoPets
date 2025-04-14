package com.example.media_service.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "profile_pictures")
public class ProfilePicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "storage_identifier", nullable = false, length = 100)
    private String storageIdentifier;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    

    public ProfilePicture() {
    }

    public ProfilePicture(Long userId, String storageIdentifier) {
        this.userId = userId;
        this.storageIdentifier = storageIdentifier;
    }


    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getStorageIdentifier() { return storageIdentifier; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setStorageIdentifier(String storageIdentifier) { this.storageIdentifier = storageIdentifier; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
