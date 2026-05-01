package co.com.report.infrastructure.entrypoints.reactiveweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class BootcampRouter {

    private static final String PATH = "/api/v1/bootcamps";

    @Bean
    public RouterFunction<ServerResponse> bootcampRoutes(BootcampHandler handler) {
        return RouterFunctions.route()
            .GET(PATH + "/max-technology-count", handler::getMaxTechnologyCount)
            .build();
    }
}