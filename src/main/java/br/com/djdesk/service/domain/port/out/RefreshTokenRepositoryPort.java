package br.com.djdesk.service.domain.port.out;

import br.com.djdesk.service.domain.model.RefreshToken;
import br.com.djdesk.service.domain.model.User;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void revokeAllByUser(User user);
}
