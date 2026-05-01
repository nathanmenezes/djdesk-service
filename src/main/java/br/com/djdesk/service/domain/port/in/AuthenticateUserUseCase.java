package br.com.djdesk.service.domain.port.in;

import br.com.djdesk.service.adapter.in.web.dto.LoginRequest;
import br.com.djdesk.service.adapter.in.web.dto.TokenResponse;

public interface AuthenticateUserUseCase {
    TokenResponse authenticate(LoginRequest request);
}
