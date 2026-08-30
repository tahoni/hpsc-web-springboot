package za.co.hpsc.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class HpscWebApplicationTest {

    // contextLoads()
    @SuppressWarnings("EmptyMethod")
    @Test
    void testContextLoads_whenSpringContextStarted_thenLoadsSuccessfully() {
    }

}
