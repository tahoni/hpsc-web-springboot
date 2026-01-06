package za.co.hpsc.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "hpsc-web", version = "1.0.0", description = "HPSC website back-end."))
@SpringBootApplication
public class HpscWebApplication {

    static void main(String[] args) {
        SpringApplication.run(HpscWebApplication.class, args);
    }

}
