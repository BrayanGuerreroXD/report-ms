package co.com.report.domain.usecase.bootcamp;

import co.com.report.domain.model.Bootcamp;
import reactor.core.publisher.Mono;

public interface SyncBootcampService {
    Mono<Bootcamp> sync(Bootcamp bootcamp);
}