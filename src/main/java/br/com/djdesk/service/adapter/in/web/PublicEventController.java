package br.com.djdesk.service.adapter.in.web;

import br.com.djdesk.service.adapter.in.web.dto.PublicEventResponse;
import br.com.djdesk.service.domain.port.in.GetPublicEventUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/public/events")
public class PublicEventController {

    private final GetPublicEventUseCase getPublicEventUseCase;

    public PublicEventController(GetPublicEventUseCase getPublicEventUseCase) {
        this.getPublicEventUseCase = getPublicEventUseCase;
    }

    @GetMapping("/{token}")
    public ResponseEntity<PublicEventResponse> getByPublicToken(@PathVariable UUID token) {
        return ResponseEntity.ok(getPublicEventUseCase.getByPublicToken(token));
    }
}
