package com.piedel.piotr.configuration.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class ConfigurationDto {

    private final String client;
    private final String version;
    private final String key;
    private final String value;
}
