package eu.planlos.pretixtonextcloudintegrator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "qnafilter")
public class QnAFilterConfiguration {
    private List<Map<String,List<String>>> qna;
}