package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class StringToPretixQnaFilterConverter implements Converter<String, PretixQnaFilter> {

    private final ObjectMapper objectMapper;

    public StringToPretixQnaFilterConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public PretixQnaFilter convert(@Nullable String source) {
        try {
            return objectMapper.readValue(source, PretixQnaFilter.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert JSON string to User object", e);
        }
    }
}