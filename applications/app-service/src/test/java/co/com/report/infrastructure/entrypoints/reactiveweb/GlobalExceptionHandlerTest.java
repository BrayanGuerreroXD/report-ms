package co.com.report.infrastructure.entrypoints.reactiveweb;

import co.com.report.model.exception.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.HttpHandlerConnector;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private WebTestClient clientFor(Throwable error) {
        GlobalExceptionHandler handler = new GlobalExceptionHandler(new ObjectMapper());
        var httpHandler = WebHttpHandlerBuilder
            .webHandler(exchange -> Mono.error(error))
            .exceptionHandler(handler)
            .build();
        return WebTestClient.bindToServer(new HttpHandlerConnector(httpHandler)).build();
    }

    @Test
    void shouldReturn400_forBadRequestException() {
        clientFor(new BadRequestException(GlobalExceptionEnum.BOOTCAMP_ALREADY_EXISTS))
            .get().uri("/any").exchange()
            .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody()
            .jsonPath("$.data.errorCode").isEqualTo("BOOTCAMP_ALREADY_EXISTS");
    }

    @Test
    void shouldReturn404_forNotFoundException() {
        clientFor(new NotFoundException(GlobalExceptionEnum.BOOTCAMP_NOT_FOUND))
            .get().uri("/any").exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.data.errorCode").isEqualTo("BOOTCAMP_NOT_FOUND");
    }

    @Test
    void shouldReturn409_forConflictException() {
        clientFor(new ConflictException(GlobalExceptionEnum.BOOTCAMP_ALREADY_EXISTS))
            .get().uri("/any").exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.data.errorCode").isEqualTo("BOOTCAMP_ALREADY_EXISTS");
    }
}