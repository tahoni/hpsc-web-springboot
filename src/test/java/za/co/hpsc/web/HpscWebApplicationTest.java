package za.co.hpsc.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ActiveProfiles("test")
@SpringBootTest
class HpscWebApplicationTest {

    // contextLoads()
    @SuppressWarnings("EmptyMethod")
    @Test
    void testContextLoads_whenSpringContextStarted_thenLoadsSuccessfully() {
    }

}
