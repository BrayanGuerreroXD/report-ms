package co.com.report.usecase.bootcamp;

import co.com.report.model.bootcamp.Bootcamp;
import co.com.report.model.bootcamp.gateways.BootcampRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SyncBootcampUseCase implements SyncBootcampService {

    private final BootcampRepository bootcampRepository;
    private final BootcampCountsCalculator countsCalculator = new BootcampCountsCalculator();

    @Override
    public Mono<Bootcamp> sync(Bootcamp bootcamp) {
        Bootcamp withCounts = countsCalculator.calculate(bootcamp);

        return bootcampRepository.findByExternalId(bootcamp.getExternalId())
            .flatMap(existing -> {
                Bootcamp updated = withCounts.toBuilder()
                    .id(existing.getId())
                    .build();
                return bootcampRepository.update(updated);
            })
            .switchIfEmpty(Mono.defer(() -> bootcampRepository.save(withCounts)));
    }
}
