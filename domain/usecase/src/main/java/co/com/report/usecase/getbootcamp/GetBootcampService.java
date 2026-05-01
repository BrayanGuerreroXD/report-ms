package co.com.report.usecase.getbootcamp;

import co.com.report.domain.model.Bootcamp;
import reactor.core.publisher.Mono;

public interface GetBootcampService {
    Mono<Bootcamp> getBootcampWithMaxTechnologyCount();
}