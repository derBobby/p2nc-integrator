package eu.planlos.p2ncintegrator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("TEST")
@ContextConfiguration(classes = {TestConfig.class}) // Add this line to specify the test context configuration
class P2NCIntegratorTest {

    @Test
    void contextLoads() {
        //Test if Spring context starts
    }
}