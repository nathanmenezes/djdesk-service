package br.com.djdesk.service.domain.service;

import br.com.djdesk.service.adapter.in.web.dto.LoginRequest;
import br.com.djdesk.service.adapter.in.web.dto.RefreshTokenRequest;
import br.com.djdesk.service.adapter.in.web.dto.TokenResponse;
import br.com.djdesk.service.domain.model.RefreshToken;
import br.com.djdesk.service.domain.model.User;
import br.com.djdesk.service.domain.port.in.AuthenticateUserUseCase;
import br.com.djdesk.service.domain.port.in.LogoutUseCase;
import br.com.djdesk.service.domain.port.in.RefreshTokenUseCase;
import br.com.djdesk.service.domain.port.out.RefreshTokenRepositoryPort;
import br.com.djdesk.service.infrastructure.security.JwtService;
import br.com.djdesk.service.infrastructure.security.UserPrincipal;
import br.com.djdesk.service.shared.enums.ErrorCode;
import br.com.djdesk.service.shared.exception.InvalidCredentialsException;
import br.com.djdesk.service.shared.exception.RefreshTokenInvalidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthenticateUserService
        implements AuthenticateUserUseCase, RefreshTokenUseCase, LogoutUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthenticateUserService.class);
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final JwtService jwtService;
    private final long refreshExpirationMs;

    public AuthenticateUserService(
            AuthenticationManager authenticationManager,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            JwtService jwtService,
            @Value("${jwt.refresh-expiration}") long refreshExpirationMs
    ) {
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.jwtService = jwtService;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Override
    @Transactional
    public TokenResponse authenticate(LoginRequest request) {
        log.info("Authentication attempt [correlationId={}]", MDC.get(CORRELATION_ID_MDC_KEY));

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException | DisabledException e) {
            throw new InvalidCredentialsException();
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = principal.getUser();

        refreshTokenRepositoryPort.revokeAllByUser(user);

        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getUserType().name());
        RefreshToken refreshToken = refreshTokenRepositoryPort.save(
                new RefreshToken(
                        user.getSlug().toString() + "-" + Instant.now().toEpochMilli(),
                        Instant.now().plusMillis(refreshExpirationMs),
                        user
                )
        );

        log.info("Authentication successful [correlationId={}]", MDC.get(CORRELATION_ID_MDC_KEY));

        return new TokenResponse(accessToken, refreshToken.getToken(), jwtService.getExpirationMs() / 1000);
    }

    @Override
    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        log.info("Token refresh attempt [correlationId={}]", MDC.get(CORRELATION_ID_MDC_KEY));

        RefreshToken storedToken = refreshTokenRepositoryPort
                .findByToken(request.refreshToken())
                .orElseThrow(RefreshTokenInvalidException::new);

        if (storedToken.isRevoked()) {
            throw new RefreshTokenInvalidException();
        }

        if (storedToken.isExpired()) {
            storedToken.revoke();
            refreshTokenRepositoryPort.save(storedToken);
            throw new RefreshTokenInvalidException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        User user = storedToken.getUser();
        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getUserType().name());

        storedToken.revoke();
        refreshTokenRepositoryPort.save(storedToken);

        RefreshToken newRefreshToken = refreshTokenRepositoryPort.save(
                new RefreshToken(
                        user.getSlug().toString() + "-" + Instant.now().toEpochMilli(),
                        Instant.now().plusMillis(refreshExpirationMs),
                        user
                )
        );

        log.info("Token refreshed successfully [correlationId={}]", MDC.get(CORRELATION_ID_MDC_KEY));

        return new TokenResponse(accessToken, newRefreshToken.getToken(), jwtService.getExpirationMs() / 1000);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        log.info("Logout attempt [correlationId={}]", MDC.get(CORRELATION_ID_MDC_KEY));

        refreshTokenRepositoryPort.findByToken(refreshToken)
                .ifPresent(token -> {
                    token.revoke();
                    refreshTokenRepositoryPort.save(token);
                });

        log.info("Logout successful [correlationId={}]", MDC.get(CORRELATION_ID_MDC_KEY));
    }
}
