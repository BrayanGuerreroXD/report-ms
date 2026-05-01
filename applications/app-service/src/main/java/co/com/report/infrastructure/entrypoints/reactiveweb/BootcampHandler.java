package co.com.report.infrastructure.entrypoints.reactiveweb;

import co.com.report.model.exception.NotFoundException;
import co.com.report.model.bootcamp.Bootcamp;
import co.com.report.usecase.bootcamp.GetBootcampService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BootcampHandler {

    private final GetBootcampService getBootcampService;
    private final BootcampDTOMapper bootcampMapper;

    public Mono<ServerResponse> getMaxTechnologyCount(ServerRequest request) {
        return getBootcampService.getBootcampWithMaxTechnologyCount()
            .map(bootcampMapper::toResponse)
            .flatMap(response -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(GenericResponseData.of(response)))
            .onErrorResume(NotFoundException.class, e -> {
                ErrorData error = ErrorData.of(e.getError());
                return ServerResponse.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(GenericResponseData.of(error));
            });
    }
}