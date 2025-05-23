package io.github.maaf72.smartthings.infra.thirdapi.translation;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.maaf72.smartthings.infra.thirdapi.translation.dto.TranslationResponse;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TranslationServiceImpl implements TranslationService {
  private static final String[] LIST_COUNTRY_ID = { "en", "id", "my", "jp" };

  @WithSpan
  public Uni<String> SingleCountryTranslate(String text, String countryID) {
    return Uni.createFrom().item(() -> 
        "[%s] %s".formatted(countryID, text)
    ).runSubscriptionOn(Infrastructure.getDefaultWorkerPool()); 
  }

  @WithSpan
  public Uni<List<TranslationResponse>> AllCountryTranslate(String text) {
    return Uni.combine().all().unis(
      Arrays.stream(LIST_COUNTRY_ID)
        .map(countryId -> SingleCountryTranslate(text, countryId)
          .map(translatedText -> new TranslationResponse(countryId, translatedText))
        )
        .collect(Collectors.toList())
    )
    .with(TranslationResponse.class, Function.identity());
  }
}
