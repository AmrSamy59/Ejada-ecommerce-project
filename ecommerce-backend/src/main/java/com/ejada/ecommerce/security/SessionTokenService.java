package com.ejada.ecommerce.security;

import com.ejada.ecommerce.entity.SessionToken;
import com.ejada.ecommerce.exception.InvalidSessionException;
import com.ejada.ecommerce.repository.SessionTokenRepository;
import com.ejada.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionTokenService {

    @Value("${jwt.refresh-expiration}")
    private Long SessionTokenDurationMs;

    private final SessionTokenRepository SessionTokenRepository;
    private final UserRepository userRepository;

    public SessionTokenService(SessionTokenRepository SessionTokenRepository, UserRepository userRepository) {
        this.SessionTokenRepository = SessionTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<SessionToken> findByToken(String token) {
        return SessionTokenRepository.findByToken(token);
    }

    public SessionToken createSessionToken(Long userId) {
        SessionToken SessionToken = new SessionToken();

        SessionToken.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
        SessionToken.setExpiryDate(Instant.now().plusMillis(SessionTokenDurationMs));
        SessionToken.setToken(UUID.randomUUID().toString());

        SessionToken = SessionTokenRepository.save(SessionToken);
        return SessionToken;
    }

    public SessionToken verifyExpiration(SessionToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            SessionTokenRepository.delete(token);
            throw new InvalidSessionException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return SessionTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
