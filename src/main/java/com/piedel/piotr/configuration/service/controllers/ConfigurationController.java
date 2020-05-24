package com.piedel.piotr.configuration.service.controllers;

import com.piedel.piotr.configuration.service.dto.ConfigurationDto;
import com.piedel.piotr.configuration.service.dto.ConfigurationDtoMapper;
import com.piedel.piotr.configuration.service.dto.KeyValueDto;
import com.piedel.piotr.configuration.service.dto.KeyValueDtoMapper;
import com.piedel.piotr.configuration.service.model.ClientVersion;
import com.piedel.piotr.configuration.service.model.Configuration;
import com.piedel.piotr.configuration.service.services.ClientWithVersionService;
import com.piedel.piotr.configuration.service.services.ConfigurationService;
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

    @GetMapping("/{client}/{version}")
    public ResponseEntity<List<KeyValueDto>> getConfiguration(@PathVariable String client,
                                                              @PathVariable String version,
                                                              @RequestHeader(value = "If-None-Match",
                                                                      required = false) String lastAcquiredConfigurationETag) {
        List<Configuration> changedConfigurations;

        if (lastAcquiredConfigurationETag != null) {
            changedConfigurations = configurationService
                    .findAllConfigurationsSinceGivenAcquisition(client, version,
                            lastAcquiredConfigurationETag);
        } else {
            changedConfigurations = configurationService
                    .findAllClientConfigurations(client, version);
        }

        List<KeyValueDto> changedConfigurationsDto = keyValueDtoMapper
                .configurationAsKeyValueDto(changedConfigurations);

        Configuration lastAcquiredConfiguration = changedConfigurations
                .get(changedConfigurations.size() - 1);

        return ResponseEntity.ok()
                .eTag(Long.toString(lastAcquiredConfiguration.getCreationDateTimeAsTimestamp()))
                .body(changedConfigurationsDto);
    }

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
