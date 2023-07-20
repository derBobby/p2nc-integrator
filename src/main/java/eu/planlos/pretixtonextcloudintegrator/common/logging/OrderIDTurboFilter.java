package eu.planlos.pretixtonextcloudintegrator.common.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;
import org.slf4j.MDC;


/**
 * Filter is used by Logback to add the orderCode to logging entries, if present
 */
public class OrderIDTurboFilter extends TurboFilter {

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        String orderCode = MDC.get("orderCode");

        // If 'orderCode' is empty or null, remove the MDC entry
        if (orderCode == null || orderCode.isEmpty()) {
            MDC.remove("orderCode");
        }

        // Allow the log event to be processed
        return FilterReply.NEUTRAL;
    }
}