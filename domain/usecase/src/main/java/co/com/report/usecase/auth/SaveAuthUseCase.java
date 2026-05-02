package co.com.report.usecase.auth;

import co.com.report.model.auth.Auth;
import co.com.report.model.auth.gateways.AuthRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class SaveAuthUseCase implements SaveAuthService {

    private final AuthRepository authRepository;

    @Override
    public Mono<Auth> save(Auth auth) {
        Auth toSave = auth.toBuilder().createdAt(LocalDateTime.now()).build();
        return authRepository.findByEmail(auth.getEmail())
            .flatMap(existing -> authRepository.deleteByEmail(existing.getEmail()))
            .then(authRepository.save(toSave));
    }
}