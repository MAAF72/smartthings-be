package io.github.maaf72.smartthings.infra.thirdapi.translation;

import java.util.ArrayList;
import java.util.List;

import io.github.maaf72.smartthings.infra.thirdapi.translation.dto.TranslationResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TranslationServiceImpl implements TranslationService {
  private static final String[] LIST_COUNTRY_ID = { "en", "id", "my", "jp" };

  public String SingleCountryTranslate(String text, String countryID) {
    return "[%s] %s".formatted(countryID, text);
  }

  public List<TranslationResponse> AllCountryTranslate(String text) {
    List<TranslationResponse> listTranslationResponse = new ArrayList<>();

    for (String countryId : LIST_COUNTRY_ID) {
      listTranslationResponse.add(new TranslationResponse(countryId, SingleCountryTranslate(text, countryId)));
    }

    return listTranslationResponse;
  }
}
