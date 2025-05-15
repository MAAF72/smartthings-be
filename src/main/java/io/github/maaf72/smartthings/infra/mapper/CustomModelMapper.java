package io.github.maaf72.smartthings.infra.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public class CustomModelMapper {
  public static class SafeNullCustomMapper extends ModelMapper {
    @Override
    public <D> D map(Object source, Class<D> destinationType) {
      if (source != null && destinationType != null) {
        return super.map(source, destinationType);
      }

      return null;
    }
  }

  private static final ModelMapper modelMapper = buildModelMapper();

  private static ModelMapper buildModelMapper() {
    ModelMapper modelMapper = new SafeNullCustomMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    return modelMapper;
  }
  

  public static ModelMapper getModelMapper() {
    return modelMapper;
  }
}