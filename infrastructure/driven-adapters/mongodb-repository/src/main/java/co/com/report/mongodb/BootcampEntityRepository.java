package co.com.report.mongodb;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampEntityRepository extends ReactiveMongoRepository<BootcampEntity, String> {
    Mono<BootcampEntity> findByExternalId(Long externalId);
    Mono<Boolean> existsByExternalId(Long externalId);
    Flux<BootcampEntity> findAllByOrderByTechnologyCountDesc();
}
