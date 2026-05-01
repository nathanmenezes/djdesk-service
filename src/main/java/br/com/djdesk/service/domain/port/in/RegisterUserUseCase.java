package br.com.djdesk.service.domain.port.in;

import br.com.djdesk.service.adapter.in.web.dto.RegisterRequest;
import br.com.djdesk.service.adapter.in.web.dto.TokenResponse;

public interface RegisterUserUseCase {
    TokenResponse register(RegisterRequest request);
}
