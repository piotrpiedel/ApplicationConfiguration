package com.piedel.piotr.configuration.service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piedel.piotr.configuration.service.dto.ConfigurationDto;
import com.piedel.piotr.configuration.service.dto.ConfigurationDtoMapper;
import com.piedel.piotr.configuration.service.model.ClientVersion;
import com.piedel.piotr.configuration.service.model.Configuration;
import com.piedel.piotr.configuration.service.services.ClientWithVersionService;
import com.piedel.piotr.configuration.service.services.ConfigurationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.piedel.piotr.configuration.service.controllers.ConfigurationControllerIntegrationTest.ControllerTestConfiguration.asJsonString;
import static com.piedel.piotr.configuration.service.controllers.ConfigurationControllerIntegrationTest.ControllerTestConfiguration.createGetConfigurationEndpoint;
import static com.piedel.piotr.configuration.service.controllers.ConfigurationControllerIntegrationTest.ControllerTestConfiguration.createPostConfigurationEndpoint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ConfigurationControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    ClientWithVersionService clientWithVersionService;

    @Autowired
    ConfigurationDtoMapper configurationDtoMapper;

    @Test
    @WithMockUser(roles = "USER")
    void getConfigurations_WhenIfNoneMatchHeaderEtagIsGiven_ReturnLatestUpdatedConfigurationDistinctByKey()
            throws Exception {
        ClientVersion clientVersion = ClientVersion.builder().client("android").version("7.2.4").build();
        clientWithVersionService.saveClientWithVersion(clientVersion);

        //given
        String getConfigurationEndpoint = createGetConfigurationEndpoint("android", "7.2.4");
        List<Configuration> configurations = ConfigurationsProvider
                .getFiveConfigurations_TwoAdsEndpoint_TwoBackgroundColors_OneFontColorWithClient(clientVersion);

        configurations
                .forEach(configuration -> configurationService.saveConfiguration(configuration));


        long firstConfigurationEtagFromList = configurations
                .get(0)
                .getCreationDateTimeAsTimestamp();

        //when
        ResultActions resultActions = mockMvc
                .perform(get(getConfigurationEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.IF_NONE_MATCH, firstConfigurationEtagFromList));

        //then
        String etagValue = getLastChangedConfigurationEtagValue(configurations);

        resultActions
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", etagValue))
                .andExpect(jsonPath("$.ads_endpoint").value("/devads_updated"))
                .andExpect(jsonPath("$.background_color").value("#005"))
                .andExpect(jsonPath("$.font_color").value("#324"));
    }

    private String getLastChangedConfigurationEtagValue(List<Configuration> configurations) {
        Configuration lastChangedConfiguration = getLastChangedConfigurationFromList(
                configurationService
                        .findAllClientConfigurations("android", "7.2.4"));

        // ETag is double quoted string - treat configuration creation date time as etag
        return '"' + String.valueOf(lastChangedConfiguration.getCreationDateTimeAsTimestamp()) + '"';
    }

    private Configuration getLastChangedConfigurationFromList(List<Configuration> configurations) {
        return configurations.get(configurations.size() - 1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addConfiguration_WhenCorrectDto_SaveToDbAndReturnStatusCreated() throws Exception {
        //given
        String addConfigurationEndpoint = createPostConfigurationEndpoint();

        String client = "android";
        String version = "4.4";
        String configurationKey = "ads_endpoint";
        String configurationValue = "/my_ads";

        ConfigurationDto configurationDto = ConfigurationDto
                .builder()
                .client(client)
                .version(version)
                .key(configurationKey)
                .value(configurationValue)
                .build();

        //when
        ResultActions resultActions = mockMvc
                .perform(post(addConfigurationEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(configurationDto)));

        resultActions
                .andExpect(status().isCreated());

        Configuration savedConfiguration = configurationService.findAllClientConfigurations(client, version).stream().findFirst().get();
        Assertions.assertEquals(configurationDto, configurationDtoMapper.asDto(savedConfiguration));

    }

    @TestConfiguration
    static class ControllerTestConfiguration {

        static final String BASE_CONFIGURATION_CONTROLLER_ENDPOINT = "/config";

        static String createPostConfigurationEndpoint() {
            return BASE_CONFIGURATION_CONTROLLER_ENDPOINT;
        }

        static String createGetConfigurationEndpoint(String client, String version) {
            return String.join("/", BASE_CONFIGURATION_CONTROLLER_ENDPOINT, client, version);
        }

        public static String asJsonString(final Object obj) {
            try {
                return new ObjectMapper().writeValueAsString(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
