package br.com.djdesk.service.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.Map;

public record CreateEventRequest(
        @NotBlank String name,
        @NotBlank String clientName,
        String type,
        Instant eventDate,
        Map<String, Object> additionalInfo
) {}
