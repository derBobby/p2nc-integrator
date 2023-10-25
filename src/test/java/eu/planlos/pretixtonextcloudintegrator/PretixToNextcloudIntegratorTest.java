package eu.planlos.pretixtonextcloudintegrator;

import eu.planlos.pretixtonextcloudintegrator.pretix.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

//TODO what imports are necessary?
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("TEST")
@ContextConfiguration(classes = {TestConfig.class}) // Add this line to specify the test context configuration
class PretixToNextcloudIntegratorTest {

    @Autowired
    private MockMvc mockMvc;

    //TODO create test GET request
    @Test
    void contextLoads() {
    }
}