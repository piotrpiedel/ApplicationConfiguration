package com.piedel.piotr.configuration.service.controllers;

import com.piedel.piotr.configuration.service.dto.ConfigurationDtoMapper;
import com.piedel.piotr.configuration.service.dto.KeyValueDtoMapper;
import com.piedel.piotr.configuration.service.services.ClientWithVersionService;
import com.piedel.piotr.configuration.service.services.ConfigurationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ConfigurationController.class)
class ConfigurationControllerTest {

    private static final String BASE_CONFIGURATION_CONTROLLER_ENDPOINT = "/config";
//    private static final String BASE_CONFIGURATION_CONTROLLER_ENDPOINT = "@GetMapping("/{client}/{version}")";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ConfigurationService configurationService;

    @MockBean
    ClientWithVersionService clientWithVersionService;

    @Test
    void getConfigurations_WhenNoConfigurationForClient_ReturnNotModified() throws Exception {
        //given no configuration
        when(configurationService
                .findAllClientConfigurations(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Collections.emptyList());

        //when
        String getConfigurationEndpoint = String.join("/", BASE_CONFIGURATION_CONTROLLER_ENDPOINT, "ios", "231");
        ResultActions resultActions = mockMvc.perform(get(getConfigurationEndpoint)
                .contentType(MediaType.APPLICATION_JSON));


        //then
        resultActions
                .andExpect(status().isNotModified());

    }

    @Test
    void getConfigurations_WhenNoIfNoneMatchHeader_ReturnAllConfigurations() {

    }

    @Test
    void getConfigurations_WhenClientNotFound_ReturnEmptyList() {

    }

    @Test
    void getConfigurations_WhenIncorrectEtagFormat_ThrowIncorrectEtagException() {

    }


    @Test
    void addConfiguration() {
    }

    @TestConfiguration
    static class ParkingControllerTestConfiguration {
        @Bean
        ConfigurationDtoMapper configurationDtoMapper() {
            return new ConfigurationDtoMapper();
        }

        @Bean
        KeyValueDtoMapper keyValueDtoMapper() {
            return new KeyValueDtoMapper();
        }
    }
}