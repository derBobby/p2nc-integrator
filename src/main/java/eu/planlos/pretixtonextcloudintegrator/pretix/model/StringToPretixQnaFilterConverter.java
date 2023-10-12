package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class StringToPretixQnaFilterConverter {

    private final ObjectMapper objectMapper;

    public StringToPretixQnaFilterConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<PretixQnaFilter> convertAll(List<String> filterList) throws JsonProcessingException {
        List<PretixQnaFilter> pretixQnaFilterList = new ArrayList<>();
        for (String filter : filterList) {
            pretixQnaFilterList.add(convert(objectMapper, filter));
        }
        return pretixQnaFilterList;
    }

    private PretixQnaFilter convert(ObjectMapper objectMapper, String filter) throws JsonProcessingException {
        return objectMapper.readValue(filter, PretixQnaFilter.class);
    }
}