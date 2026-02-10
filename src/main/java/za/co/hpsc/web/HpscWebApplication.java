package za.co.hpsc.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "hpsc-web", version = "2.0.1",
        description = "HPSC website backend."))
@SpringBootApplication
public class HpscWebApplication {

    static void main(String[] args) {
        SpringApplication.run(HpscWebApplication.class, args);
    }

}
