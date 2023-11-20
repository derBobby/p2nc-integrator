package eu.planlos.p2ncintegrator.pretix.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Converter
@Slf4j
public class PretixQnaFilterMapToStringDBConverter implements AttributeConverter<Map<String, List<String>>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, List<String>> pretixQnaFilterMap) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(pretixQnaFilterMap);
        } catch (JsonProcessingException e) {
            log.error("Error serializing PretixQnaFilter to JSON", e);
            return null; // You can handle the error as needed
        }
    }

    @Override
    public Map<String, List<String>> convertToEntityAttribute(String dbDataJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(dbDataJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Error deserializing JSON to PretixQnaFilter", e);
            return null; // You can handle the error as needed
        }
    }
}