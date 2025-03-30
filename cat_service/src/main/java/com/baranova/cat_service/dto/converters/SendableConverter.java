package com.baranova.cat_service.dto.converters;

import lombok.extern.slf4j.Slf4j;
import com.baranova.cat_service.entity.Sendable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Slf4j
public class SendableConverter {


    public static String toJson(Sendable sendable) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(sendable);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert from Sendable to JSON" + e.getMessage());
            return null;
        }
    }

    public static Sendable fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.readValue(json, Sendable.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert from JSON to Sendable" + e.getMessage());
            return null;
        }
    }
}
