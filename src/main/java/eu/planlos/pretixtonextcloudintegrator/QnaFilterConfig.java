package eu.planlos.pretixtonextcloudintegrator;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "qnafilter")
public record QnaFilterConfig(List<Map<String, List<String>>> qnaList) {
}