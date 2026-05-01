package co.com.report.infrastructure.drivenadapters.mongodb.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampEntityRepository extends ReactiveMongoRepository<BootcampEntity, String> {
    Mono<BootcampEntity> findByExternalId(Long externalId);
    Mono<Boolean> existsByExternalId(Long externalId);
    Flux<BootcampEntity> findAllByOrderByTechnologyCountDesc();
}
