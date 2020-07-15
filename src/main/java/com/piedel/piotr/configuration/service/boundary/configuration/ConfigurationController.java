package com.piedel.piotr.configuration.service.boundary.configuration;

import com.piedel.piotr.configuration.service.boundary.exceptions.IncorrectEtagException;
import com.piedel.piotr.configuration.service.domain.clientwithversion.ClientVersion;
import com.piedel.piotr.configuration.service.domain.clientwithversion.ClientWithVersionService;
import com.piedel.piotr.configuration.service.domain.configuration.Configuration;
import com.piedel.piotr.configuration.service.domain.configuration.ConfigurationService;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/config")
public final class ConfigurationController {

    private final ConfigurationService configurationService;
    private final ClientWithVersionService clientWithVersionService;
    private final ConfigurationDtoMapper configurationDtoMapper;
    private final ConfigurationToResponseMapper configurationToResponseMapper;

    public ConfigurationController(
            ConfigurationService configurationService,
            ClientWithVersionService clientWithVersionService,
            ConfigurationDtoMapper configurationDtoMapper,
            ConfigurationToResponseMapper configurationToResponseMapper) {

        this.configurationService = configurationService;
        this.clientWithVersionService = clientWithVersionService;
        this.configurationDtoMapper = configurationDtoMapper;
        this.configurationToResponseMapper = configurationToResponseMapper;
    }

    @GetMapping("/{client}/{version}")
    public ResponseEntity<String> getChangedConfigurations(
            @PathVariable String client,
            @PathVariable String version,
            @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false)
                    String lastAcquiredConfigurationETag) {
        List<Configuration> configurations;

        if (isEtagHeaderPresent(lastAcquiredConfigurationETag)) {
            try {
                configurations = findConfigurationsForGivenClientVersionChangedAfterEtag(client,
                        version, lastAcquiredConfigurationETag);
            } catch (IncorrectEtagException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            configurations = findAllConfigurations(client, version);
        }

        if (configurations.isEmpty()) {
            return createResponseNotModified();
        }

        return mapConfigurationsToJsonObject(configurations);
    }

    private boolean isEtagHeaderPresent(String lastAcquiredConfigurationETag) {
        return lastAcquiredConfigurationETag != null;
    }

    private List<Configuration> findConfigurationsForGivenClientVersionChangedAfterEtag(
            String client,
            String version,
            String lastAcquiredConfigurationETag)
            throws IncorrectEtagException {
        return configurationService
                .findAllChangedConfigurationsSinceGivenAcquisition(client, version,
                        lastAcquiredConfigurationETag);
    }

    private List<Configuration> findAllConfigurations(String client, String version) {
        return configurationService
                .findAllClientConfigurations(client, version);
    }

    private ResponseEntity<String> createResponseNotModified() {
        return ResponseEntity
                .status(HttpStatus.NOT_MODIFIED)
                .build();
    }

    private ResponseEntity<String> mapConfigurationsToJsonObject(
            List<Configuration> changedConfigurations) {
        JSONObject response = configurationToResponseMapper
                .configurationsToJsonObjectValues(changedConfigurations);

        return ResponseEntity
                .ok()
                .eTag(getEtagFromLatestConfiguration(changedConfigurations))
                .body(response.toString());
    }

    private String getEtagFromLatestConfiguration(
            List<Configuration> changedConfigurations) {
        Configuration config = getLatestConfigurationFromList(changedConfigurations);
        return Long.toString(config.getCreationDateTimeAsTimestamp());
    }

    private Configuration getLatestConfigurationFromList(
            List<Configuration> changedConfigurations) {
        return changedConfigurations.get(changedConfigurations.size() - 1);
    }

    @PostMapping("")
    public ResponseEntity<Object> addConfiguration(@RequestBody ConfigurationDto configurationDto) {
        ClientVersion clientVersion = clientWithVersionService
                .findOrCreate(configurationDto.getClient(), configurationDto.getVersion());

        Configuration configurationToSave = configurationDtoMapper
                .asEntity(configurationDto, clientVersion);
        configurationService.saveConfiguration(configurationToSave);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}