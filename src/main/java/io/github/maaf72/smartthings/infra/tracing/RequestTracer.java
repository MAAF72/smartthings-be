package io.github.maaf72.smartthings.infra.tracing;

import java.util.UUID;

import io.opentelemetry.api.trace.Span;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RequestTracer {
    private final UUID id;
    private final Span rootSpan;
}