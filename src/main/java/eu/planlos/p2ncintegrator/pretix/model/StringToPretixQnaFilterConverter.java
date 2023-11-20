package eu.planlos.p2ncintegrator.pretix.model;

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

        if(filterList == null) {
            return new ArrayList<>();
        }

        List<PretixQnaFilter> pretixQnaFilterList = new ArrayList<>();
        for (String filter : filterList) {
            pretixQnaFilterList.add(convert(filter));
        }
        return pretixQnaFilterList;
    }

    private PretixQnaFilter convert(String filter) throws JsonProcessingException {
        return objectMapper.readValue(filter, PretixQnaFilter.class);
    }
}