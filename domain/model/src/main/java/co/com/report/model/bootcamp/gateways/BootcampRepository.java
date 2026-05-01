package co.com.report.model.bootcamp.gateways;

import co.com.report.model.bootcamp.Bootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampRepository {
    Mono<Bootcamp> save(Bootcamp bootcamp);
    Mono<Bootcamp> update(Bootcamp bootcamp);
    Mono<Bootcamp> findById(String id);
    Mono<Bootcamp> findByExternalId(Long externalId);
    Flux<Bootcamp> findAll();
    Flux<Bootcamp> findAllByOrderByTechnologyCountDesc();
    Mono<Void> deleteById(String id);
}
