package eu.planlos.p2ncintegrator.pretix.config;

import eu.planlos.p2ncintegrator.pretix.PretixTestDataUtility;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PretixEventFilterConfigTest extends PretixTestDataUtility {

    @Test
    public void isConfiguredByPropertiesFile_returnsAppropriate() {
        PretixEventFilterConfig config = new PretixEventFilterConfig("properties", Collections.emptyList());
        assertTrue(config.isPropertiesSourceConfigured());
        assertFalse(config.isUserSourceConfigured());
    }

    @Test
    public void isConfiguredByUser_returnsAppropriate() {
        PretixEventFilterConfig config = new PretixEventFilterConfig("user", Collections.emptyList());
        assertFalse(config.isPropertiesSourceConfigured());
        assertTrue(config.isUserSourceConfigured());
    }
}