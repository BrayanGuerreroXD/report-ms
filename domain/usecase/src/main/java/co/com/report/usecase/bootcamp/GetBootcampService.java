package co.com.report.usecase.bootcamp;

import co.com.report.model.bootcamp.Bootcamp;
import reactor.core.publisher.Mono;

public interface GetBootcampService {
    Mono<Bootcamp> getBootcampWithMaxTechnologyCount();
}
