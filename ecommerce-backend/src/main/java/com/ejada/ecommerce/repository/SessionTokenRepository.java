package com.ejada.ecommerce.repository;

import com.ejada.ecommerce.entity.SessionToken;
import com.ejada.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionTokenRepository extends JpaRepository<SessionToken, Long> {
    Optional<SessionToken> findByToken(String token);
    int deleteByUser(User user);
}
