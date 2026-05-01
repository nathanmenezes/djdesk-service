package br.com.djdesk.service.adapter.out.persistence;

import br.com.djdesk.service.domain.model.RefreshToken;
import br.com.djdesk.service.domain.model.User;
import br.com.djdesk.service.domain.port.out.RefreshTokenRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RefreshTokenPersistenceAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    public RefreshTokenPersistenceAdapter(RefreshTokenJpaRepository refreshTokenJpaRepository) {
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenJpaRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenJpaRepository.findByToken(token);
    }

    @Override
    public void revokeAllByUser(User user) {
        refreshTokenJpaRepository.revokeAllActiveByUser(user);
    }
}
