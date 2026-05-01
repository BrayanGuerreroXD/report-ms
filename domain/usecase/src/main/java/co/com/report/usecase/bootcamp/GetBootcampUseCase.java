package co.com.report.usecase.bootcamp;

import co.com.report.model.bootcamp.Bootcamp;
import co.com.report.model.bootcamp.gateways.BootcampRepository;
import co.com.report.model.exception.GlobalExceptionEnum;
import co.com.report.model.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetBootcampUseCase implements GetBootcampService {

    private final BootcampRepository bootcampRepository;

    @Override
    public Mono<Bootcamp> getBootcampWithMaxTechnologyCount() {
        return bootcampRepository.findAllByOrderByTechnologyCountDesc()
            .next()
            .switchIfEmpty(Mono.error(new NotFoundException(GlobalExceptionEnum.BOOTCAMP_NOT_FOUND)));
    }
}
