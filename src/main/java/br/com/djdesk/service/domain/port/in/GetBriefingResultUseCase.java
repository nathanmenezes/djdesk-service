package br.com.djdesk.service.domain.port.in;

import br.com.djdesk.service.adapter.in.web.dto.BriefingResultResponse;

import java.util.Optional;
import java.util.UUID;

public interface GetBriefingResultUseCase {

    Optional<BriefingResultResponse> getByEventSlug(UUID eventSlug);
}
