package br.com.djdesk.service.domain.service;

import br.com.djdesk.service.adapter.in.web.dto.RegisterRequest;
import br.com.djdesk.service.adapter.in.web.dto.TokenResponse;
import br.com.djdesk.service.domain.model.RefreshToken;
import br.com.djdesk.service.domain.model.User;
import br.com.djdesk.service.domain.enums.UserType;
import br.com.djdesk.service.domain.port.in.RegisterUserUseCase;
import br.com.djdesk.service.domain.port.out.RefreshTokenRepositoryPort;
import br.com.djdesk.service.domain.port.out.UserRepositoryPort;
import br.com.djdesk.service.infrastructure.security.JwtService;
import br.com.djdesk.service.shared.exception.EmailAlreadyRegisteredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterUserService.class);
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    private final UserRepositoryPort userRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long refreshExpirationMs;

    public RegisterUserService(
            UserRepositoryPort userRepositoryPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${jwt.refresh-expiration}") long refreshExpirationMs
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Override
    @Transactional
    public TokenResponse register(RegisterRequest request) {
        log.info("User registration initiated [correlationId={}]", MDC.get(CORRELATION_ID_MDC_KEY));

        if (userRepositoryPort.existsByEmail(request.email())) {
            throw new EmailAlreadyRegisteredException();
        }

        User user = new User(
                request.fullName(),
                request.artisticName(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.phone(),
                request.profilePhotoUrl(),
                request.bio(),
                UserType.DJ
        );

        User saved = userRepositoryPort.save(user);
        log.info("User registered successfully [correlationId={}]", MDC.get(CORRELATION_ID_MDC_KEY));

        String accessToken = jwtService.generateAccessToken(saved.getEmail(), saved.getUserType().name());
        RefreshToken refreshToken = refreshTokenRepositoryPort.save(
                new RefreshToken(
                        saved.getSlug().toString() + "-" + Instant.now().toEpochMilli(),
                        Instant.now().plusMillis(refreshExpirationMs),
                        saved
                )
        );

        return new TokenResponse(accessToken, refreshToken.getToken(), jwtService.getExpirationMs() / 1000);
    }
}
