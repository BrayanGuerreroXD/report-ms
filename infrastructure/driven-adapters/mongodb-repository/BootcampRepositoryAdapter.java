package co.com.report.infrastructure.drivenadapters.mongodb.repository;

import co.com.report.model.bootcamp.Bootcamp;
import co.com.report.model.bootcamp.gateways.BootcampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class BootcampRepositoryAdapter implements BootcampRepository {

    private final BootcampEntityRepository entityRepository;
    private final BootcampEntityMapper mapper;

    @Override
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        return entityRepository.save(mapper.toEntity(bootcamp))
                .map(mapper::toModel);
    }

    @Override
    public Mono<Bootcamp> update(Bootcamp bootcamp) {
        return entityRepository.save(mapper.toEntity(bootcamp))
                .map(mapper::toModel);
    }

    @Override
    public Mono<Bootcamp> findById(String id) {
        return entityRepository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public Mono<Bootcamp> findByExternalId(Long externalId) {
        return entityRepository.findByExternalId(externalId)
                .map(mapper::toModel);
    }

    @Override
    public Flux<Bootcamp> findAll() {
        return entityRepository.findAll()
                .map(mapper::toModel);
    }

    @Override
    public Flux<Bootcamp> findAllByOrderByTechnologyCountDesc() {
        return entityRepository.findAllByOrderByTechnologyCountDesc()
                .map(mapper::toModel);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return entityRepository.deleteById(id);
    }
}
