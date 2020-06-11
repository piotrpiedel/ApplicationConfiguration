package com.piedel.piotr.configuration.service.controllers;

import com.piedel.piotr.configuration.service.model.ClientVersion;
import com.piedel.piotr.configuration.service.model.Configuration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class ConfigurationsProvider {

    static List<Configuration> getTwoConfigurations() {
        List<Configuration> configurations = new ArrayList<>();
        configurations.add(new Configuration(0L,
                "ads_endpoint",
                "/devads", new Timestamp(1000L),
                mock(ClientVersion.class)
        ));
        configurations.add(new Configuration(1L,
                "background_color",
                "#001", new Timestamp(2000L),
                mock(ClientVersion.class)
        ));
        return configurations;
    }

}
