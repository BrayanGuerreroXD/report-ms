package co.com.report.infrastructure.entrypoints.reactiveweb;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class BootcampRouter {

    private static final String PATH = "/api/v1/bootcamps";

    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/bootcamps/max-technology-count",
            method = RequestMethod.GET,
            beanClass = BootcampHandler.class,
            beanMethod = "getMaxTechnologyCount",
            produces = MediaType.APPLICATION_JSON_VALUE,
            operation = @Operation(
                operationId = "getBootcampWithMaxTechnologyCount",
                summary = "Get bootcamp with maximum number of technologies",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Bootcamp found",
                        content = @Content(schema = @Schema(implementation = BootcampResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Bootcamp not found")
                }
            )
        )
    })
    @Bean
    public RouterFunction<ServerResponse> bootcampRoutes(BootcampHandler handler) {
        return RouterFunctions.route()
            .GET(PATH + "/max-technology-count", handler::getMaxTechnologyCount)
            .build();
    }
}