package com.piedel.piotr.configuration.service.boundary.configuration;

import com.piedel.piotr.configuration.service.domain.clientwithversion.ClientVersion;
import com.piedel.piotr.configuration.service.domain.configuration.Configuration;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationDtoMapper {

    public Configuration asEntity(
            ConfigurationDto configurationDto,
            ClientVersion clientVersion) {
        return createConfigurationToSave(configurationDto, clientVersion);
    }

    private Configuration createConfigurationToSave(
            ConfigurationDto configurationDto,
            ClientVersion clientVersion) {
        return Configuration
                .builder()
                .key(configurationDto.getKey())
                .value(configurationDto.getValue())
                .clientVersion(clientVersion)
                .build();
    }

    public ConfigurationDto asDto(Configuration configuration) {
        return ConfigurationDto.builder()
                .client(configuration.getClient())
                .version(configuration.getVersion())
                .key(configuration.getKey())
                .value(configuration.getValue())
                .build();
    }
}
