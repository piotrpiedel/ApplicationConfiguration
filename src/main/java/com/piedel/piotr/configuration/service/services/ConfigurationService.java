package com.piedel.piotr.configuration.service.services;

import com.piedel.piotr.configuration.service.model.ClientVersion;
import com.piedel.piotr.configuration.service.model.Configuration;
import com.piedel.piotr.configuration.service.repositories.ConfigurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ConfigurationService {


    private final ConfigurationRepository configurationRepository;
    private final ClientWithVersionService clientWithVersionService;

    public ConfigurationService(ConfigurationRepository configurationRepository,
                                ClientWithVersionService clientWithVersionService) {
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
        throw new ResponseStatusException(NOT_FOUND, "Unable to find client");
    }

    public List<Configuration> findAllConfigurationsSinceGivenAcquisition(String client,
                                                                          String version, String lastAcquiredConfigurationETag) {

        Optional<ClientVersion> clientFromDB = clientWithVersionService.findClientWithVersion(client, version);
        if (clientFromDB.isPresent()) {
            Timestamp timestamp = new Timestamp(getDate(lastAcquiredConfigurationETag).getTime());
            return configurationRepository.findAllByClientVersionIdAfterGivenAcquisition(clientFromDB.get().getId(), timestamp);
        }
        throw new ResponseStatusException(NOT_FOUND, "Unable to find client");
    }

    private Date getDate(String lastAcquiredConfigurationETag) {
        try {
            return new Date(Long.parseLong(lastAcquiredConfigurationETag));
        } catch (NumberFormatException numberFormatException) {
            throw new ResponseStatusException(BAD_REQUEST, "Given etag is incorrect", numberFormatException);
        }
    }
}
