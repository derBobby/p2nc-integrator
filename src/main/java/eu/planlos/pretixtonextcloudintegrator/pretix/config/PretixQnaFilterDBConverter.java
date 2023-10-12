//package eu.planlos.pretixtonextcloudintegrator.pretix.config;
//
//import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixQnaFilter;
//import jakarta.persistence.AttributeConverter;
//import jakarta.persistence.Converter;
//
//@Converter
//public class PretixQnaFilterDBConverter implements AttributeConverter<PretixQnaFilter, String> {
//
//    @Override
//    public String convertToDatabaseColumn(PretixQnaFilter pretixQnaFilter) {
//        return pretixQnaFilter.toString();
//    }
//
//    @Override
//    public PretixQnaFilter convertToEntityAttribute(String dbData) {
//        return PretixQnaFilter.fromString(dbData);
//    }
//}
