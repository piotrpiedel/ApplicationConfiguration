package com.piedel.piotr.configuration.service.services;

import com.piedel.piotr.configuration.service.model.Etag;
import com.piedel.piotr.configuration.service.repositories.ConfigurationRepository;
import org.springframework.stereotype.Service;

@Service
public class EtagService {

    private final ConfigurationRepository configurationRepository;


    public EtagService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    public Etag generateEtag() {
        return new Etag(2l, "W/2");
    }

}
