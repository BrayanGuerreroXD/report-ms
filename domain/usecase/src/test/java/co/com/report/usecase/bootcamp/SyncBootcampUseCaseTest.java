package co.com.report.usecase.bootcamp;

import co.com.report.model.bootcamp.Bootcamp;
import co.com.report.model.bootcamp.gateways.BootcampRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SyncBootcampUseCaseTest {

    @Mock
    private BootcampRepository bootcampRepository;

    private SyncBootcampUseCase createUseCase() {
        return new SyncBootcampUseCase(bootcampRepository);
    }

    @Test
    void sync_whenBootcampNotExists_shouldSave() {
        Bootcamp input = Bootcamp.builder()
            .externalId(1L)
            .name("Test")
            .build();
        Bootcamp saved = input.toBuilder()
            .capacityCount(0)
            .personCount(0)
            .technologyCount(0)
            .build();

        when(bootcampRepository.findByExternalId(1L)).thenReturn(Mono.empty());
        when(bootcampRepository.save(any(Bootcamp.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(createUseCase().sync(input))
            .expectNextMatches(result -> result.getExternalId().equals(1L))
            .verifyComplete();
    }

    @Test
    void sync_whenBootcampExists_shouldUpdate() {
        Bootcamp input = Bootcamp.builder()
            .externalId(1L)
            .name("Test")
            .build();
        Bootcamp existing = Bootcamp.builder().id("existing-id").externalId(1L).name("Old").build();

        when(bootcampRepository.findByExternalId(1L)).thenReturn(Mono.just(existing));
        when(bootcampRepository.update(any(Bootcamp.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(createUseCase().sync(input))
            .expectNextMatches(result -> result.getId().equals("existing-id") && result.getExternalId().equals(1L))
            .verifyComplete();
    }
}
