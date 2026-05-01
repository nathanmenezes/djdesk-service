package br.com.djdesk.service.domain.port.in;

import br.com.djdesk.service.adapter.in.web.dto.RefreshTokenRequest;
import br.com.djdesk.service.adapter.in.web.dto.TokenResponse;

public interface RefreshTokenUseCase {
    TokenResponse refresh(RefreshTokenRequest request);
}
