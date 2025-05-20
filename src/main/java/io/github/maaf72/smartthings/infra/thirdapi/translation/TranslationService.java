package io.github.maaf72.smartthings.infra.thirdapi.translation;

import java.util.List;

import io.github.maaf72.smartthings.infra.thirdapi.translation.dto.TranslationResponse;
import io.smallrye.mutiny.Uni;

public interface TranslationService {
  Uni<String> SingleCountryTranslate(String text, String countryID);

  Uni<List<TranslationResponse>> AllCountryTranslate(String text);
}
