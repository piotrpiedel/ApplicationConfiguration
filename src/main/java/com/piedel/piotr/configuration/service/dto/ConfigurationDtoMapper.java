package com.piedel.piotr.configuration.service.dto;

import com.piedel.piotr.configuration.service.model.ClientVersion;
import com.piedel.piotr.configuration.service.model.Configuration;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationDtoMapper {

    public Configuration convertToEntity(ConfigurationDto configurationDto, ClientVersion clientVersion) {
        return createConfigurationToSave(configurationDto, clientVersion);
    }

    private Configuration createConfigurationToSave(ConfigurationDto configurationDto, ClientVersion clientVersion) {
        return Configuration
                .builder()
                .key(configurationDto.getKey())
                .value(configurationDto.getValue())
                .clientVersion(clientVersion)
                .build();
    }

    public ConfigurationDto convertToDto(Configuration configuration) {
        return ConfigurationDto.builder()
                .client(configuration.getClient())
                .version(configuration.getVersion())
                .key(configuration.getKey())
                .value(configuration.getValue())
                .build();
    }
}
