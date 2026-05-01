package co.com.report.infrastructure.entrypoints.reactiveweb;

import co.com.report.model.bootcamp.*;
import co.com.report.usecase.bootcamp.GetBootcampService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.HttpHandlerConnector;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BootcampRouterTest {

    @Mock
    private GetBootcampService getBootcampService;

    private BootcampHandler handler;
    private RouterFunction<ServerResponse> router;
    private WebTestClient client;

    @BeforeEach
    void setUp() {
        BootcampDTOMapper mapper = new BootcampDTOMapperImpl();
        handler = new BootcampHandler(getBootcampService, mapper);
        router = new BootcampRouter().bootcampRoutes(handler);
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(new ObjectMapper());

        var httpHandler = WebHttpHandlerBuilder
            .webHandler(RouterFunctions.toWebHandler(router))
            .exceptionHandler(exceptionHandler)
            .build();

        client = WebTestClient.bindToServer(new HttpHandlerConnector(httpHandler)).build();
    }

    @Test
    void GET_maxTechnologyCount_shouldReturn200() {
        Bootcamp bootcamp = Bootcamp.builder()
            .id("1")
            .externalId(1L)
            .name("Test")
            .technologyCount(5)
            .build();

        when(getBootcampService.getBootcampWithMaxTechnologyCount()).thenReturn(Mono.just(bootcamp));

        client.get().uri("/api/v1/bootcamps/max-technology-count")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.data.name").isEqualTo("Test");
    }
}