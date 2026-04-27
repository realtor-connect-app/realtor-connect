package com.makurohashami.realtorconnect.scheduler.service.user;

import com.makurohashami.realtorconnect.config.UserConfiguration;
import com.makurohashami.realtorconnect.repository.ConfirmationTokenRepository;
import com.makurohashami.realtorconnect.repository.UserRepository;
import java.time.Instant;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSchedulerService {

    private final UserConfiguration userConfiguration;
    private final UserRepository userRepository;
    private final ConfirmationTokenRepository tokenRepository;

    @Transactional
    @Scheduled(cron = "${user.scheduler.delete-unverified-users-cron}")
    public void deleteUnverifiedUsers() {
        Instant time = ZonedDateTime.now()
                .minusDays(userConfiguration.getTimeToVerifyEmailInDays())
                .toInstant();
        userRepository.deleteAllByCreatedAtIsBeforeAndEmailVerifiedFalse(time);
    }

    @Transactional
    @Scheduled(cron = "${user.scheduler.delete-unused-tokens-cron}")
    public void deleteOldTokens() {
        Instant time = ZonedDateTime.now()
                .minusDays(userConfiguration.getTokenTtlInDays())
                .toInstant();
        tokenRepository.deleteAllByCreatedAtBefore(time);
    }

}
