package co.com.report.domain.usecase.bootcamp;

import co.com.report.domain.model.*;
import co.com.report.domain.usecase.bootcamp.SyncBootcampUseCase;
import co.com.report.infrastructure.drivenadapters.mongodb.repository.BootcampEntity;
import co.com.report.infrastructure.drivenadapters.mongodb.repository.BootcampEntityMapper;
import co.com.report.infrastructure.drivenadapters.mongodb.repository.BootcampEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncBootcampUseCaseTest {

    @Mock
    private BootcampEntityRepository bootcampRepository;

    @Mock
    private BootcampEntityMapper bootcampMapper;

    @Mock
    private BootcampCountsCalculator countsCalculator;

    @InjectMocks
    private SyncBootcampUseCase useCase;

    @Test
    void sync_whenBootcampNotExists_shouldSave() {
        Bootcamp input = Bootcamp.builder().externalId(1L).name("Test").build();
        Bootcamp withCounts = input.toBuilder().capacityCount(1).personCount(1).technologyCount(2).build();
        BootcampEntity entity = new BootcampEntity();
        BootcampEntity savedEntity = new BootcampEntity();

        when(countsCalculator.calculate(input)).thenReturn(withCounts);
        when(bootcampRepository.findByExternalId(1L)).thenReturn(Mono.empty());
        when(bootcampMapper.toEntity(withCounts)).thenReturn(entity);
        when(bootcampRepository.save(entity)).thenReturn(Mono.just(savedEntity));
        when(bootcampMapper.toModel(savedEntity)).thenReturn(withCounts);

        StepVerifier.create(useCase.sync(input))
            .expectNext(withCounts)
            .verifyComplete();
    }
}