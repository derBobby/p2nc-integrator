package eu.planlos.pretixtonextcloudintegrator.pretix.model;

import jakarta.persistence.AttributeConverter;

public class StringToPretixQnaFilterDBConverter implements AttributeConverter<PretixQnaFilter, String> {

    @Override
    public String convertToDatabaseColumn(PretixQnaFilter pretixQnaFilter) {
        return pretixQnaFilter.toString();
    }

    @Override
    public PretixQnaFilter convertToEntityAttribute(String dbData) {
        return PretixQnaFilter.fromString(dbData);
    }
}