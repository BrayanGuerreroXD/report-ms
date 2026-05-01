package co.com.report.usecase.auth;

import co.com.report.model.auth.Auth;
import co.com.report.model.auth.gateways.AuthRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetAuthUseCase implements GetAuthService {

    private final AuthRepository authRepository;

    @Override
    public Mono<Auth> getByToken(String token) {
        return authRepository.findByToken(token);
    }
}
