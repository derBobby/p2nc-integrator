package eu.planlos.pretixtonextcloudintegrator.pretix.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixQnaFilter;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@ConfigurationPropertiesBinding
public class PretixQnasConverter implements Converter<String, List<PretixQnaFilter>> {

    private final ObjectMapper objectMapper;

    public PretixQnasConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<PretixQnaFilter> convert(String source) {
        try {
            Map<String, List<String>> qnasMap = objectMapper.readValue(source, new TypeReference<Map<String, List<String>>>() {});

            // Convert the qnasMap to a list of PretixQnaFilter instances
            // You need to adapt this logic based on your PretixQnaFilter class

            // For example:
            List<PretixQnaFilter> qnaFilters = qnasMap.entrySet().stream()
                    .map(entry -> new PretixQnaFilter(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

            return qnaFilters;
        } catch (IOException e) {
            // Handle conversion error
            return null;
        }
    }
}
