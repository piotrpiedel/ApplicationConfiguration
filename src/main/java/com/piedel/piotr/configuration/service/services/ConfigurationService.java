package com.piedel.piotr.configuration.service.services;

import com.piedel.piotr.configuration.service.exceptions.IncorrectEtagException;
import com.piedel.piotr.configuration.service.model.ClientVersion;
import com.piedel.piotr.configuration.service.model.Configuration;
import com.piedel.piotr.configuration.service.repositories.ConfigurationRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigurationService {


    private final ConfigurationRepository configurationRepository;
    private final ClientWithVersionService clientWithVersionService;

    public ConfigurationService(ConfigurationRepository configurationRepository, ClientWithVersionService clientWithVersionService) {
        this.configurationRepository = configurationRepository;
        this.clientWithVersionService = clientWithVersionService;
    }

    public Configuration saveConfiguration(Configuration configuration) {
        return configurationRepository.save(configuration);
    }

    public List<Configuration> findAllClientConfigurations(String client, String version) {
        Optional<ClientVersion> clientFromDB = clientWithVersionService.findClientWithVersion(client, version);
        if (clientFromDB.isPresent()) {
            return configurationRepository.findAllByClientVersionId(clientFromDB.get().getId());
        }
        return Collections.emptyList();
    }

    public List<Configuration> findAllConfigurationsSinceGivenAcquisition(String client,
                                                                          String version,
                                                                          String lastAcquiredConfigurationETag) throws IncorrectEtagException {

        Optional<ClientVersion> clientWithVersion = clientWithVersionService.findClientWithVersion(client, version);
        if (clientWithVersion.isPresent()) {
            Timestamp timestamp = new Timestamp(getDate(lastAcquiredConfigurationETag).getTime());
            return configurationRepository
                    .findAllByClientVersionIdAfterGivenCreationDate(clientWithVersion.get().getId(), timestamp);
        }
        return Collections.emptyList();
    }

    private Date getDate(String lastAcquiredConfigurationETag) throws IncorrectEtagException {
        try {
            return new Date(Long.parseLong(lastAcquiredConfigurationETag));
        } catch (NumberFormatException numberFormatException) {
            throw new IncorrectEtagException("Given etag is incorrect", numberFormatException);
        }
    }
}
