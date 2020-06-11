package com.piedel.piotr.configuration.service.dto;

import com.piedel.piotr.configuration.service.model.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class KeyValueDtoMapper {

    public KeyValueDto configurationAsKeyValueDto(Configuration configuration) {
        return KeyValueDto
                .builder()
                .key(configuration.getKey())
                .value(configuration.getValue())
                .build();
    }

    public List<KeyValueDto> configurationAsKeyValueDto(List<Configuration> configuration) {
        return configuration
                .stream()
                .map(this::configurationAsKeyValueDto)
                .collect(Collectors.toList());
    }


}
