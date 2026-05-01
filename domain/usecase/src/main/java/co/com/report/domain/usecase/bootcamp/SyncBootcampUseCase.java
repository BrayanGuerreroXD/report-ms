package co.com.report.domain.usecase.bootcamp;

import co.com.report.domain.model.Bootcamp;
import co.com.report.domain.usecase.bootcamp.SyncBootcampService;
import co.com.report.infrastructure.drivenadapters.mongodb.repository.BootcampEntityMapper;
import co.com.report.infrastructure.drivenadapters.mongodb.repository.BootcampEntityRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SyncBootcampUseCase implements SyncBootcampService {

    private final BootcampEntityRepository bootcampRepository;
    private final BootcampEntityMapper bootcampMapper;
    private final BootcampCountsCalculator countsCalculator;

    @Override
    public Mono<Bootcamp> sync(Bootcamp bootcamp) {
        Bootcamp withCounts = countsCalculator.calculate(bootcamp);

        return bootcampRepository.findByExternalId(bootcamp.getExternalId())
            .flatMap(existing -> {
                Bootcamp updated = withCounts.toBuilder()
                    .id(existing.getId())
                    .build();
                return bootcampRepository.save(bootcampMapper.toEntity(updated))
                    .map(bootcampMapper::toModel);
            })
            .switchIfEmpty(Mono.defer(() ->
                bootcampRepository.save(bootcampMapper.toEntity(withCounts))
                    .map(bootcampMapper::toModel)
            ));
    }
}