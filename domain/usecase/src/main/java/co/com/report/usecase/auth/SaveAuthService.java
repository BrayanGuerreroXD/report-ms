package co.com.report.usecase.auth;

import co.com.report.model.auth.Auth;
import reactor.core.publisher.Mono;

public interface SaveAuthService {
    Mono<Auth> save(Auth auth);
}