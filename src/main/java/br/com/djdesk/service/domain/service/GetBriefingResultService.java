package br.com.djdesk.service.domain.service;

import br.com.djdesk.service.adapter.in.web.dto.BriefingResultResponse;
import br.com.djdesk.service.domain.port.in.GetBriefingResultUseCase;
import br.com.djdesk.service.domain.port.out.BriefingRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class GetBriefingResultService implements GetBriefingResultUseCase {

    private final BriefingRepositoryPort briefingRepositoryPort;

    public GetBriefingResultService(BriefingRepositoryPort briefingRepositoryPort) {
        this.briefingRepositoryPort = briefingRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BriefingResultResponse> getByEventSlug(UUID eventSlug) {
        return briefingRepositoryPort.findResultByEventSlug(eventSlug)
                .map(BriefingResultResponse::from);
    }
}
