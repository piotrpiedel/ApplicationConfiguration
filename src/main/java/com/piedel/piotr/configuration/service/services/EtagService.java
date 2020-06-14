package com.piedel.piotr.configuration.service.services;

import com.piedel.piotr.configuration.service.model.Etag;
import com.piedel.piotr.configuration.service.repositories.ConfigurationRepository;
import org.springframework.stereotype.Service;

/**
 * @deprecated Deprecated cause i am not using this anymore - should delete but I don't
 * want to lose the idea of this - maybe I will figure it out how to use it properly
 * Remove this if not
 */
@Service
@Deprecated
public class EtagService {

    private final ConfigurationRepository configurationRepository;

    public EtagService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    public Etag generateEtag() {
        return new Etag(2L, "W/2");
    }

}
