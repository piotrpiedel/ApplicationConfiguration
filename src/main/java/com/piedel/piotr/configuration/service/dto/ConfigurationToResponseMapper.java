package com.piedel.piotr.configuration.service.dto;

import com.piedel.piotr.configuration.service.model.ClientVersion;
import com.piedel.piotr.configuration.service.model.Configuration;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfigurationToResponseMapper {

    public JSONObject asJsonObjectProperties(List<Configuration> configurations) {
        JSONObject jsonObject = new JSONObject();
        for (Configuration configuration : configurations) {
            jsonObject.put(configuration.getKey(), configuration.getValue());
        }
        return jsonObject;
    }

    public Configuration convertToEntity(
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

    public ConfigurationDto convertToDto(Configuration configuration) {
        return ConfigurationDto.builder()
                .client(configuration.getClient())
                .version(configuration.getVersion())
                .key(configuration.getKey())
                .value(configuration.getValue())
                .build();
    }
}
