package eu.planlos.pretixtonextcloudintegrator.common.web;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class PretixContext {
    @Setter
    @Getter
    private String event;
}