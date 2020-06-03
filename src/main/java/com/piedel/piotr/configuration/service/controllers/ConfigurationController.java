package com.piedel.piotr.configuration.service.controllers;

import com.piedel.piotr.configuration.service.dto.ConfigurationDto;
import com.piedel.piotr.configuration.service.dto.ConfigurationDtoMapper;
import com.piedel.piotr.configuration.service.dto.KeyValueDto;
import com.piedel.piotr.configuration.service.dto.KeyValueDtoMapper;
import com.piedel.piotr.configuration.service.exceptions.IncorrectEtagException;
import com.piedel.piotr.configuration.service.model.ClientVersion;
import com.piedel.piotr.configuration.service.model.Configuration;
import com.piedel.piotr.configuration.service.services.ClientWithVersionService;
import com.piedel.piotr.configuration.service.services.ConfigurationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final KeyValueDtoMapper keyValueDtoMapper;

    public ConfigurationController(ConfigurationService configurationService,
                                   ClientWithVersionService clientWithVersionService,
                                   ConfigurationDtoMapper configurationDtoMapper,
                                   KeyValueDtoMapper keyValueDtoMapper) {

        this.configurationService = configurationService;
        this.clientWithVersionService = clientWithVersionService;
        this.configurationDtoMapper = configurationDtoMapper;
        this.keyValueDtoMapper = keyValueDtoMapper;
    }

//    @PreAuthorize(value = "hasRole('user') or hasRole('admin')")
    @GetMapping("/{client}/{version}")
    public ResponseEntity<List<KeyValueDto>> getConfigurations(@PathVariable String client,
                                                               @PathVariable String version,
                                                               @RequestHeader(value = "If-None-Match",
                                                                       required = false) String lastAcquiredConfigurationETag) {

        List<Configuration> changedConfigurations;

        if (lastAcquiredConfigurationETag != null) {
            try {
                changedConfigurations = findConfigurationsForGivenClientVersionAndEtag(client, version, lastAcquiredConfigurationETag);
            } catch (IncorrectEtagException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } else {
            changedConfigurations = findAllConfigurations(client, version);
        }

        if (changedConfigurations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return mapConfigurationsAsResponseEntityConfigurationDto(changedConfigurations);
    }

    private List<Configuration> findConfigurationsForGivenClientVersionAndEtag(String client, String version, String lastAcquiredConfigurationETag)
            throws IncorrectEtagException {
        return configurationService
                .findAllConfigurationsSinceGivenAcquisition(client, version, lastAcquiredConfigurationETag);
    }

    private List<Configuration> findAllConfigurations(String client, String version) {
        return configurationService
                .findAllClientConfigurations(client, version);
    }

    private Configuration getLastAcquiredConfiguration(List<Configuration> changedConfigurations) {
        return changedConfigurations
                .get(changedConfigurations.size() - 1);
    }

    private ResponseEntity<List<KeyValueDto>> mapConfigurationsAsResponseEntityConfigurationDto(List<Configuration> changedConfigurations) {
        List<KeyValueDto> changedConfigurationsDto = keyValueDtoMapper
                .configurationAsKeyValueDto(changedConfigurations);
        Configuration lastAcquiredConfiguration = getLastAcquiredConfiguration(changedConfigurations);

        return ResponseEntity
                .ok()
                .eTag(Long.toString(lastAcquiredConfiguration.getCreationDateTimeAsTimestamp()))
                .body(changedConfigurationsDto);
    }

//    @PreAuthorize(value = "hasRole('admin')")
    @PostMapping("")
    public ResponseEntity<Object> addConfiguration(@RequestBody ConfigurationDto configurationDto) {
        ClientVersion clientVersion = clientWithVersionService
                .findOrCreate(configurationDto.getClient(), configurationDto.getVersion());

        Configuration configurationToSave = configurationDtoMapper
                .convertToEntity(configurationDto, clientVersion);

        configurationService.saveConfiguration(configurationToSave);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}