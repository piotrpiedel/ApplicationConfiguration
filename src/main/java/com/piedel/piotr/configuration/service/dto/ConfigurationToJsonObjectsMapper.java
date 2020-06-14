package com.piedel.piotr.configuration.service.dto;

import com.piedel.piotr.configuration.service.model.Configuration;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfigurationToJsonObjectsMapper {

    public JSONObject asKeyValuesPropertiesJsonObject(List<Configuration> configurations)
            throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (Configuration configuration : configurations) {
            jsonObject.put(configuration.getKey(), configuration.getValue());
        }
        return jsonObject;
    }
}
