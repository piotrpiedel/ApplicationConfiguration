package com.piedel.piotr.configuration.service.boundary.configuration;

import com.piedel.piotr.configuration.service.domain.configuration.Configuration;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfigurationToResponseMapper {

    public JSONObject configurationsToJsonObjectValues(List<Configuration> configurations) {
        JSONObject jsonObject = new JSONObject();
        for (Configuration configuration : configurations) {
            jsonObject.put(configuration.getKey(), configuration.getValue());
        }
        return jsonObject;
    }
}
