package com.makurohashami.realtorconnect.repository;

import com.makurohashami.realtorconnect.entity.user.ConfirmationToken;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, UUID> {

    void deleteByUserId(Long userId);

    Optional<ConfirmationToken> findByUserId(Long userId);

    void deleteAllByCreatedAtBefore(Instant createdAt);

}
