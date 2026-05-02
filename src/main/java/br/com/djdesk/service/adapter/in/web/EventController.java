package br.com.djdesk.service.adapter.in.web;

import br.com.djdesk.service.adapter.in.web.dto.BriefingResultResponse;
import br.com.djdesk.service.adapter.in.web.dto.CreateEventRequest;
import br.com.djdesk.service.adapter.in.web.dto.EventResponse;
import br.com.djdesk.service.domain.model.Event;
import br.com.djdesk.service.domain.port.in.CreateEventUseCase;
import br.com.djdesk.service.domain.port.in.FindEventUseCase;
import br.com.djdesk.service.domain.port.in.GetBriefingResultUseCase;
import br.com.djdesk.service.domain.port.in.ListEventsUseCase;
import br.com.djdesk.service.infrastructure.security.UserPrincipal;
import br.com.djdesk.service.shared.enums.ErrorCode;
import br.com.djdesk.service.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/events")
public class EventController {

    private final CreateEventUseCase createEventUseCase;
    private final ListEventsUseCase listEventsUseCase;
    private final FindEventUseCase findEventUseCase;
    private final GetBriefingResultUseCase getBriefingResultUseCase;

    public EventController(
            CreateEventUseCase createEventUseCase,
            ListEventsUseCase listEventsUseCase,
            FindEventUseCase findEventUseCase,
            GetBriefingResultUseCase getBriefingResultUseCase
    ) {
        this.createEventUseCase = createEventUseCase;
        this.listEventsUseCase = listEventsUseCase;
        this.findEventUseCase = findEventUseCase;
        this.getBriefingResultUseCase = getBriefingResultUseCase;
    }

    @PostMapping
    public ResponseEntity<EventResponse> create(
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Event event = createEventUseCase.create(request, principal.getUser().getSlug());
        return ResponseEntity.status(HttpStatus.CREATED).body(EventResponse.from(event));
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> list(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<EventResponse> events = listEventsUseCase.listByDj(principal.getUser().getSlug())
                .stream()
                .map(EventResponse::from)
                .toList();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<EventResponse> findBySlug(@PathVariable UUID slug) {
        Event event = findEventUseCase.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.EVENT_NOT_FOUND, "Event not found"));
        return ResponseEntity.ok(EventResponse.from(event));
    }

    @GetMapping("/{slug}/briefing-result")
    public ResponseEntity<BriefingResultResponse> getBriefingResult(@PathVariable UUID slug) {
        return getBriefingResultUseCase.getByEventSlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
