package com.piedel.piotr.configuration.service.boundary.configuration;

import com.piedel.piotr.configuration.service.boundary.exceptions.IncorrectEtagException;
import com.piedel.piotr.configuration.service.domain.clientwithversion.ClientWithVersionService;
import com.piedel.piotr.configuration.service.domain.configuration.Configuration;
import com.piedel.piotr.configuration.service.domain.configuration.ConfigurationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;

import static com.piedel.piotr.configuration.service.boundary.configuration.ConfigurationControllerTest.ControllerTestConfiguration.createGetConfigurationEndpoint;
import static com.piedel.piotr.configuration.service.boundary.configuration.ConfigurationsProvider.getTwoConfigurations;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ConfigurationController.class)
class ConfigurationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ConfigurationService configurationService;

    @MockBean
    ClientWithVersionService clientWithVersionService;

    @Test
    @WithMockUser(roles = "USER")
    void getConfigurations_WhenNoConfigurationForClient_ReturnNotModified() throws Exception {
        //given
        String getConfigurationEndpoint = createGetConfigurationEndpoint("android", "231");

        when(configurationService
                .findAllClientConfigurations(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Collections.emptyList());

        //when
        ResultActions resultActions = mockMvc
                .perform(get(getConfigurationEndpoint)
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andExpect(status().isNotModified());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getConfigurations_WhenIfNoneMatchHeaderEmpty_ReturnAllConfigurationsWithEtagFromLastConfig()
            throws Exception {
        //given
        String configurationsGetEndpoint = createGetConfigurationEndpoint("ios", "232");
        List<Configuration> configurations = getTwoConfigurations();
        when(configurationService
                .findAllClientConfigurations(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(configurations);

        //when
        ResultActions resultActions = mockMvc
                .perform(get(configurationsGetEndpoint)
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        String value = getLastChangedConfigurationEtagValue(configurations);

        resultActions
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", value))
                .andExpect(jsonPath("$.ads_endpoint").value("/devads"))
                .andExpect(jsonPath("$.background_color").value("#001"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getConfigurations_WhenClientNotFound_ReturnEmptyList() throws Exception {
        //given
        String getConfigurationEndpoint = createGetConfigurationEndpoint("ios", "289");
        when(configurationService
                .findAllClientConfigurations(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Collections.emptyList());

        //when
        ResultActions resultActions = mockMvc
                .perform(get(getConfigurationEndpoint)
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andExpect(status().isNotModified());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getConfigurations_WhenIncorrectEtagFormat_ThrowIncorrectEtagException() throws Exception {
        //given
        String getConfigurationEndpoint = createGetConfigurationEndpoint("windows", "10");
        when(configurationService
                .findAllChangedConfigurationsSinceGivenAcquisition(
                        Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new IncorrectEtagException("Test should fail"));
        String incorrectETag = "NotANumberEtag";

        //when
        ResultActions resultActions = mockMvc
                .perform(get(getConfigurationEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.IF_NONE_MATCH, incorrectETag));
        //then
        resultActions
                .andExpect(status().isBadRequest());
    }

    private String getLastChangedConfigurationEtagValue(List<Configuration> configurations) {
        Configuration lastChangedConfiguration = getLastChangedConfigurationFromList(
                configurations);
        // ETag is double quoted string - treat configuration creation date time as etag
        return '"' + String.valueOf(lastChangedConfiguration.getCreationDateTimeAsTimestamp())
                + '"';
    }

    private Configuration getLastChangedConfigurationFromList(List<Configuration> configurations) {
        return configurations.get(configurations.size() - 1);
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

        @Bean
        ConfigurationDtoMapper configurationDtoMapper() {
            return new ConfigurationDtoMapper();
        }

        @Bean
        ConfigurationToResponseMapper configurationToJsonObjectsMapper() {
            return new ConfigurationToResponseMapper();
        }
    }
}