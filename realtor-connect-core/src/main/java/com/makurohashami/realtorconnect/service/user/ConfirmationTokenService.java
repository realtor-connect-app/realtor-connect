package com.makurohashami.realtorconnect.service.user;

import com.makurohashami.realtorconnect.annotation.Loggable;
import com.makurohashami.realtorconnect.entity.user.ConfirmationToken;
import com.makurohashami.realtorconnect.entity.user.User;
import com.makurohashami.realtorconnect.repository.ConfirmationTokenRepository;
import com.makurohashami.realtorconnect.util.exception.ResourceNotFoundException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Loggable
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository tokenRepository;

    @Transactional
    public UUID createToken(User user) {
        ConfirmationToken token = ConfirmationToken.builder()
                .user(user)
                .build();
        ConfirmationToken savedToken = tokenRepository.save(token);
        user.setConfirmationToken(savedToken);
        return savedToken.getToken();
    }

    @Transactional(readOnly = true)
    public User findUserByToken(UUID token) {
        return tokenRepository.findById(token)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User not found by token: '%s'", token)))
                .getUser();
    }

    @Transactional
    public void deleteToken(UUID token) {
        tokenRepository.deleteById(token);
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        tokenRepository.deleteByUserId(userId);
    }

}
