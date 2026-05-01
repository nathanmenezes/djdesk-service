package br.com.djdesk.service.domain.port.out;

import br.com.djdesk.service.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    boolean existsByEmail(String email);
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findBySlug(UUID slug);
}
