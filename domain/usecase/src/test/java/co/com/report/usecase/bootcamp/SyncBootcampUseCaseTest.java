package co.com.report.usecase.bootcamp;

import co.com.report.model.bootcamp.Bootcamp;
import co.com.report.model.bootcamp.gateways.BootcampRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SyncBootcampUseCaseTest {

    @Mock
    private BootcampRepository bootcampRepository;

    @Mock
    private BootcampCountsCalculator countsCalculator;

    @InjectMocks
    private SyncBootcampUseCase useCase;

    @Test
    void sync_whenBootcampNotExists_shouldSave() {
        Bootcamp input = Bootcamp.builder().externalId(1L).name("Test").build();
        Bootcamp withCounts = input.toBuilder().capacityCount(1).personCount(1).technologyCount(2).build();

        when(countsCalculator.calculate(input)).thenReturn(withCounts);
        when(bootcampRepository.findByExternalId(1L)).thenReturn(Mono.empty());
        when(bootcampRepository.save(withCounts)).thenReturn(Mono.just(withCounts));

        StepVerifier.create(useCase.sync(input))
            .expectNext(withCounts)
            .verifyComplete();
    }

    @Test
    void sync_whenBootcampExists_shouldUpdate() {
        Bootcamp input = Bootcamp.builder().externalId(1L).name("Test").build();
        Bootcamp withCounts = input.toBuilder().capacityCount(1).personCount(1).technologyCount(2).build();
        Bootcamp existing = Bootcamp.builder().id("existing-id").externalId(1L).name("Old").build();
        Bootcamp updated = withCounts.toBuilder().id("existing-id").build();

        when(countsCalculator.calculate(input)).thenReturn(withCounts);
        when(bootcampRepository.findByExternalId(1L)).thenReturn(Mono.just(existing));
        when(bootcampRepository.update(any(Bootcamp.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.sync(input))
            .expectNextMatches(result -> result.getId().equals("existing-id") && result.getExternalId().equals(1L))
            .verifyComplete();
    }
}
