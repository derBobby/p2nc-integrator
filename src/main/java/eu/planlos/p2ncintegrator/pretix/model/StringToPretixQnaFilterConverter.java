package eu.planlos.p2ncintegrator.pretix.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StringToPretixQnaFilterConverter {

    private final ObjectMapper objectMapper;

    public StringToPretixQnaFilterConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<PretixQnaFilter> convertAll(List<String> filterList) throws JsonProcessingException {

        if(filterList == null) {
            log.debug("Not converting: List is null");
            return new ArrayList<>();
        }

        List<PretixQnaFilter> pretixQnaFilterList = new ArrayList<>();
        for (String filter : filterList) {
            pretixQnaFilterList.add(convert(filter));
        }
        return pretixQnaFilterList;
    }

    private PretixQnaFilter convert(String filterString) throws JsonProcessingException {
        PretixQnaFilter filter = objectMapper.readValue(filterString, PretixQnaFilter.class);
        log.debug("Converting: \"{}\" to \"{}\"", filterString, filter);
        return filter;
    }
}