package br.com.djdesk.service.domain.port.in;

public interface LogoutUseCase {
    void logout(String refreshToken);
}
