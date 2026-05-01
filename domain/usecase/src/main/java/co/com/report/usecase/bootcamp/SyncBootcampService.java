package co.com.report.usecase.bootcamp;

import co.com.report.model.bootcamp.Bootcamp;
import reactor.core.publisher.Mono;

public interface SyncBootcampService {
    Mono<Bootcamp> sync(Bootcamp bootcamp);
}
