package com.piedel.piotr.configuration.service.boundary.configuration;

import com.piedel.piotr.configuration.service.domain.clientwithversion.ClientVersion;
import com.piedel.piotr.configuration.service.domain.configuration.Configuration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

@SuppressWarnings("ALL")
public class ConfigurationsProvider {

    static List<Configuration> getTwoConfigurations() {
        List<Configuration> configurations = new ArrayList<>();
        addConfiguration(configurations, 0L, "ads_endpoint", "/devads", 1000L);
        addConfiguration(configurations, 1L, "background_color", "#001", 2000L);
        return configurations;
    }

    static List<Configuration> getFiveConfigurations_TwoAdsEndpoint_TwoBackgroundColors_OneFontColor() {
        List<Configuration> configurations = new ArrayList<>();
        addConfiguration(configurations, 0L, "ads_endpoint", "/devads", 1000L);
        addConfiguration(configurations, 1L, "background_color", "#001", 3000L);
        addConfiguration(configurations, 3L, "background_color", "#005", 6000L);
        addConfiguration(configurations, 4L, "ads_endpoint", "/devads_updated", 11000L);
        addConfiguration(configurations, 5L, "font_color", "#324", 13000L);
        return configurations;
    }

    public static List<Configuration> getFiveConfigurations_TwoAdsEndpoint_TwoBackgroundColors_OneFontColorWithClient(
            ClientVersion clientVersion) {
        List<Configuration> configurations = new ArrayList<>();
        addConfiguration(configurations, 0L, "ads_endpoint", "/devads", 1000L, clientVersion);
        addConfiguration(configurations, 1L, "background_color", "#001", 3000L, clientVersion);
        addConfiguration(configurations, 3L, "background_color", "#005", 6000L, clientVersion);
        addConfiguration(
                configurations, 4L, "ads_endpoint", "/devads_updated", 11000L, clientVersion);
        addConfiguration(configurations, 5L, "font_color", "#324", 13000L, clientVersion);
        return configurations;
    }

    private static void addConfiguration(
            List<Configuration> configurations,
            long configId,
            String configKey, String configValue, long creationDate, ClientVersion clientVersionn) {
        configurations.add(Configuration
                .builder()
                .id(configId)
                .key(configKey)
                .value(configValue)
                .creationDate(new Timestamp(creationDate))
                .clientVersion(clientVersionn)
                .build());
    }

    private static void addConfiguration(
            List<Configuration> configurations,
            long configId,
            String configKey, String configValue, long creationDate) {
        configurations.add(Configuration
                .builder()
                .id(configId)
                .key(configKey)
                .value(configValue)
                .creationDate(new Timestamp(creationDate))
                .clientVersion(mock(ClientVersion.class))
                .build());
    }

}
