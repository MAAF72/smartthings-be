package io.github.maaf72.smartthings.infra.thirdapi.translation;

import java.util.List;

import io.github.maaf72.smartthings.infra.thirdapi.translation.dto.TranslationResponse;

public interface TranslationService {
  String SingleCountryTranslate(String text, String countryID);

  List<TranslationResponse> AllCountryTranslate(String text);
}
