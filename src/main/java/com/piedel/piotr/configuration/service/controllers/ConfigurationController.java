package com.piedel.piotr.configuration.service.controllers;

import com.piedel.piotr.configuration.service.dto.ConfigurationDto;
import com.piedel.piotr.configuration.service.dto.ConfigurationDtoMapper;
import com.piedel.piotr.configuration.service.dto.ConfigurationToResponseMapper;
import com.piedel.piotr.configuration.service.exceptions.IncorrectEtagException;
import com.piedel.piotr.configuration.service.model.ClientVersion;
import com.piedel.piotr.configuration.service.model.Configuration;
import com.piedel.piotr.configuration.service.services.ClientWithVersionService;
import com.piedel.piotr.configuration.service.services.ConfigurationService;
import org.json.JSONException;
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
    public ResponseEntity<String> getConfigurations(
            @PathVariable String client,
            @PathVariable String version,
            @RequestHeader(value = HttpHeaders.IF_NONE_MATCH,
                    required = false) String lastAcquiredConfigurationETag) throws JSONException {

        List<Configuration> changedConfigurations;

        if (lastAcquiredConfigurationETag != null) {
            try {
                changedConfigurations = findConfigurationsForGivenClientVersionAndETag(client,
                        version, lastAcquiredConfigurationETag);
            } catch (IncorrectEtagException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            changedConfigurations = findAllConfigurations(client, version);
        }

        if (changedConfigurations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return mapConfigurationsAsResponseEntityConfigurationDto(changedConfigurations);
    }

    private List<Configuration> findConfigurationsForGivenClientVersionAndETag(
            String client,
            String version, String lastAcquiredConfigurationETag)
            throws IncorrectEtagException {
        return configurationService
                .findAllChangedConfigurationsSinceGivenAcquisition(client, version,
                        lastAcquiredConfigurationETag);
    }

    private List<Configuration> findAllConfigurations(String client, String version) {
        return configurationService
                .findAllClientConfigurations(client, version);
    }

    private Configuration getLastAcquiredConfiguration(List<Configuration> changedConfigurations) {
        return changedConfigurations
                .get(changedConfigurations.size() - 1);
    }

    private ResponseEntity<String> mapConfigurationsAsResponseEntityConfigurationDto(
            List<Configuration> changedConfigurations) {

        Configuration lastAcquiredConfiguration = getLastAcquiredConfiguration(
                changedConfigurations);

        JSONObject response = configurationToResponseMapper
                .asJsonObjectProperties(changedConfigurations);
        return ResponseEntity
                .ok()
                .eTag(Long.toString(lastAcquiredConfiguration.getCreationDateTimeAsTimestamp()))
                .body(response.toString());
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