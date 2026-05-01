package br.com.djdesk.service.adapter.in.web.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {}
